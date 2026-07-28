package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.PaginationResponse;
import com.smart.ecommerce.dto.order.GuestSalesDtos.*;
import com.smart.ecommerce.dto.order.OrderDtos.OrderResponse;
import com.smart.ecommerce.dto.product.ProductDtos.ProductResponse;
import com.smart.ecommerce.entity.*;
import com.smart.ecommerce.enums.*;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.repository.*;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeSalesLinkService {
  private final EmployeeSalesLinkRepository links;
  private final CustomerContextService context;
  private final TokenHashService tokenHashes;
  private final ProductService productService;
  private final ProductRepository products;
  private final OrderRepository orders;
  private final OrderNumberGenerator numbers;
  private final OrderCalculationService calculation;
  private final InventoryService inventory;
  private final OrderMapper orderMapper;
  private final SecureRandom random = new SecureRandom();

  @Transactional
  public SalesLinkResponse create(CreateSalesLinkRequest request) {
    User employee = context.currentCustomer();
    if (employee.getRole() != Role.ADMIN && employee.getRole() != Role.SUB_ADMIN)
      throw new IllegalArgumentException("sales_link.error.employee_required");
    byte[] bytes = new byte[32];
    random.nextBytes(bytes);
    String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    EmployeeSalesLink link = new EmployeeSalesLink();
    link.setTokenHash(tokenHashes.sha256(token));
    link.setEmployee(employee);
    link.setExpiresAt(request.expiresAt());
    link = links.save(link);
    return new SalesLinkResponse(link.getId(), token,
                                 "/api/v1/public/sales-links/" + token +
                                     "/products",
                                 link.getExpiresAt(), link.isActive());
  }

  @Transactional(readOnly = true)
  public PaginationResponse<ProductResponse> products(String token,
                                                       Long categoryId,
                                                       Boolean featured,
                                                       Pageable pageable) {
    requireValid(token);
    return productService.listPublic(categoryId, featured, pageable);
  }

  @Transactional(readOnly = true)
  public ProductResponse product(String token, Long productId) {
    requireValid(token);
    return productService.getPublic(productId);
  }

  @Transactional
  public OrderResponse order(String token, GuestOrderRequest request) {
    EmployeeSalesLink link = requireValid(token);
    Map<Long, Integer> quantities = request.items().stream().collect(
        Collectors.toMap(GuestOrderItemRequest::productId,
                         GuestOrderItemRequest::quantity, Integer::sum));
    List<Long> ids = quantities.keySet().stream().sorted().toList();
    Map<Long, Product> locked = products.lockWithImagesByIdIn(ids).stream()
                                    .collect(Collectors.toMap(Product::getId,
                                                              Function.identity()));
    if (locked.size() != ids.size())
      throw new ResourceNotFoundException("order.error.product_not_found");

    Order order = new Order();
    order.setOrderNumber(numbers.generate());
    order.setSalesLink(link);
    order.setReferredByUser(link.getEmployee());
    order.setStatus(OrderStatus.PENDING);
    order.setRecipientName(request.name().trim());
    order.setPhoneNumber(request.phoneNumber());
    order.setCity(request.city());
    order.setLatitude(request.latitude());
    order.setLongitude(request.longitude());
    order.setArea(request.area());
    order.setStreet(request.street());
    order.setAdditionalDirections(request.additionalDirections());
    order.setCustomerNote(request.customerNote());

    BigDecimal subtotal = BigDecimal.ZERO.setScale(3);
    int totalItems = 0;
    String currency = null;
    for (Long id : ids) {
      Product product = locked.get(id);
      int quantity = quantities.get(id);
      validate(product, quantity);
      String productCurrency = CurrencyCode.defaultIfBlank(product.getCurrency());
      if (currency != null && !currency.equals(productCurrency))
        throw new IllegalArgumentException("order.error.mixed_currencies");
      currency = productCurrency;
      BigDecimal unit = calculation.money(price(product));
      BigDecimal lineTotal = calculation.line(unit, quantity);
      OrderItem item = new OrderItem();
      item.setOrder(order);
      item.setProductId(id);
      item.setProductName(product.getNameEn());
      item.setProductImageUrl(primaryImage(product));
      item.setQuantity(quantity);
      item.setUnitPrice(unit);
      item.setCurrency(currency);
      item.setLineTotal(lineTotal);
      order.getItems().add(item);
      subtotal = subtotal.add(lineTotal);
      totalItems += quantity;
    }
    order.setCurrency(currency);
    order.setSubtotal(calculation.money(subtotal));
    order.setDeliveryFee(calculation.deliveryFee());
    order.setDiscountAmount(calculation.discount());
    order.setTotalAmount(calculation.money(order.getSubtotal()
                                               .add(order.getDeliveryFee())
                                               .subtract(order.getDiscountAmount())));
    order.setTotalItems(totalItems);
    addHistory(order, link.getEmployee());
    Order saved = orders.saveAndFlush(order);
    ids.forEach(id -> inventory.recordOrderCreated(
        locked.get(id), quantities.get(id), saved, link.getEmployee()));
    return orderMapper.toResponse(saved);
  }

  private EmployeeSalesLink requireValid(String token) {
    EmployeeSalesLink link = links.findByTokenHash(tokenHashes.sha256(token))
                                 .orElseThrow(() -> new ResourceNotFoundException(
                                     "sales_link.error.not_found"));
    if (!link.isActive() || (link.getExpiresAt() != null &&
                             !link.getExpiresAt().isAfter(Instant.now())))
      throw new ResourceNotFoundException("sales_link.error.not_found");
    return link;
  }

  private void validate(Product product, int quantity) {
    if (product.isDeleted())
      throw new ResourceNotFoundException("order.error.product_not_found");
    if (!product.isActive())
      throw new IllegalArgumentException("order.error.product_inactive");
    if (product.getStockQuantity() < quantity)
      throw new IllegalArgumentException("order.error.insufficient_stock");
    if (price(product) == null || price(product).signum() < 0)
      throw new IllegalArgumentException("order.error.invalid_price");
  }
  private BigDecimal price(Product p) {
    return p.getDiscountPrice() == null ? p.getPrice() : p.getDiscountPrice();
  }
  private String primaryImage(Product p) {
    return p.getImages().stream().filter(ProductImage::isPrimaryImage).findFirst()
        .or(() -> p.getImages().stream().findFirst())
        .map(ProductImage::getImageUrl).orElse(null);
  }
  private void addHistory(Order order, User employee) {
    OrderStatusHistory history = new OrderStatusHistory();
    history.setOrder(order);
    history.setNewStatus(OrderStatus.PENDING);
    history.setChangedByUserId(employee.getId());
    history.setChangedByRole("GUEST");
    history.setNote("order.history.created_from_employee_link");
    history.setChangedAt(Instant.now());
    order.getStatusHistory().add(history);
  }
}
