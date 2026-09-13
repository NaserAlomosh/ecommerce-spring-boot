package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.PaginationResponse;
import com.smart.ecommerce.dto.inventory.InventoryDtos.*;
import com.smart.ecommerce.entity.*;
import com.smart.ecommerce.enums.InventoryMovementType;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.repository.*;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {
  private final ProductRepository products;
  private final InventoryHistoryRepository history;
  private final CustomerContextService ctx;
  private final InventoryMapper mapper;
  @Transactional
  public void recordProductCreated(Product p, int initialStock, User by) {
    set(p, InventoryMovementType.PRODUCT_CREATED, initialStock, null, by,
        "inventory.movement.product_created");
  }
  @Transactional
  public void recordProductStockUpdated(Product p, int newStock, User by,
                                        String note) {
    set(p, InventoryMovementType.PRODUCT_UPDATED, newStock, null, by, note);
  }
  @Transactional
  public void recordAdminAdjustment(Long productId, int change, String note) {
    if (note == null || note.isBlank())
      throw new IllegalArgumentException(
          "inventory.error.adjustment_note_required");
    User by = ctx.currentCustomer();
    Product p = products.lockWithImagesById(productId).orElseThrow(
        ()
            -> new ResourceNotFoundException(
                "inventory.error.product_not_found"));
    apply(p, InventoryMovementType.ADMIN_ADJUSTMENT, change, null, by, note);
  }
  @Transactional
  public void recordOrderCreated(Product p, int orderedQty, Order o, User by) {
    apply(p, InventoryMovementType.ORDER_CREATED, -orderedQty, o, by,
          "inventory.movement.order_created");
  }
  @Transactional
  public void recordOrderCancelled(Product p, int qty, Order o, User by,
                                   String note) {
    apply(p, InventoryMovementType.ORDER_CANCELLED, qty, o, by,
          note == null ? "inventory.movement.order_cancelled" : note);
  }
  @Transactional
  public void recordOrderStatusAdjustment(Product p, int change, Order o,
                                          User by, String note) {
    apply(p, InventoryMovementType.ORDER_STATUS_ADJUSTMENT, change, o, by,
          note == null || note.isBlank()
              ? "inventory.movement.order_status_adjustment"
              : note);
  }
  @Transactional
  public void recordReturnedProduct(Product p, int qty, Order o, User by,
                                    String note) {
    apply(p, InventoryMovementType.PRODUCT_RETURNED, qty, o, by,
          note == null ? "inventory.movement.product_returned" : note);
  }
  @Transactional(readOnly = true)
  public PaginationResponse<InventoryHistoryResponse>
  search(Long productId, InventoryMovementType type, Long customerId,
         String orderNumber, Instant from, Instant to, Pageable pageable) {
    Pageable p =
        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                       pageable.getSort().isSorted()
                           ? pageable.getSort()
                           : Sort.by(Sort.Direction.DESC, "createdAt"));
    return PaginationResponse.from(history
                                       .search(productId, type, customerId,
                                               blank(orderNumber), from, to, p)
                                       .map(mapper::toResponse));
  }
  @Transactional(readOnly = true)
  public List<InventoryHistoryResponse> productHistory(Long productId) {
    if (!products.existsById(productId))
      throw new ResourceNotFoundException("inventory.error.product_not_found");
    return history.findByProductIdOrderByCreatedAtDesc(productId)
        .stream()
        .map(mapper::toResponse)
        .toList();
  }
  private void set(Product p, InventoryMovementType t, int newStock, Order o,
                   User by, String note) {
    apply(p, t, newStock - p.getStockQuantity(), o, by, note);
  }
  private void apply(Product p, InventoryMovementType t, int change, Order o,
                     User by, String note) {
    record(p, t, p.getStockQuantity() + change, change, o, by, note);
    p.setStockQuantity(p.getStockQuantity() + change);
  }
  private void record(Product p, InventoryMovementType t, int after, int change,
                      Order o, User by, String note) {
    int before = after - change;
    if (after < 0)
      throw new IllegalArgumentException("inventory.error.negative_stock");
    if (t != InventoryMovementType.ORDER_STATUS_ADJUSTMENT && o != null &&
        o.getId() != null &&
        history.existsByOrderIdAndProductIdAndMovementType(o.getId(), p.getId(),
                                                           t))
      throw new IllegalArgumentException(
          "inventory.error.duplicate_order_movement");
    InventoryHistory h = new InventoryHistory();
    h.setProduct(p);
    h.setMovementType(t);
    h.setQuantityBefore(before);
    h.setQuantityChange(change);
    h.setQuantityAfter(after);
    h.setOrder(o);
    if (o != null) {
      h.setOrderNumber(o.getOrderNumber());
      if (o.getCustomer() != null) {
        h.setCustomerId(o.getCustomer().getId());
        h.setCustomerNameSnapshot(o.getCustomer().getFirstName() + " " +
                                  o.getCustomer().getLastName());
      }
    }
    h.setPerformedBy(by);
    h.setNote(note);
    h.setMovementAt(Instant.now());
    history.save(h);
  }
  private String blank(String s) { return s == null || s.isBlank() ? null : s; }
}
