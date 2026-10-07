package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.PaginationResponse;
import com.smart.ecommerce.dto.review.ReviewDtos.*;
import com.smart.ecommerce.entity.*;
import com.smart.ecommerce.enums.*;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.repository.*;
import java.math.*;
import java.time.Instant;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
  private static final int SCALE = 2;
  private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
  private final CustomerContextService ctx;
  private final OrderRepository orders;
  private final OrderItemRepository orderItems;
  private final ProductRepository products;
  private final ReviewRepository reviews;
  private final ReviewMapper mapper;

  @Transactional
  public ReviewResponse create(String orderNumber, Long orderItemId,
                               CreateReviewRequest r) {
    User customer = ctx.currentCustomer();
    if (customer.getRole() != Role.CUSTOMER)
      throw new AccessDeniedException("review.error.customer_required");
    Order order = orders.findByOrderNumber(orderNumber)
                      .orElseThrow(()
                                       -> new ResourceNotFoundException(
                                           "review.error.order_not_found"));
    if (!order.getCustomer().getId().equals(customer.getId()))
      throw new AccessDeniedException("review.error.own_order_only");
    if (order.getStatus() != OrderStatus.COMPLETED)
      throw new IllegalArgumentException("review.error.completed_only");
    OrderItem item =
        orderItems.findById(orderItemId)
            .orElseThrow(()
                             -> new ResourceNotFoundException(
                                 "review.error.order_item_not_found"));
    if (!item.getOrder().getId().equals(order.getId()))
      throw new IllegalArgumentException("review.error.order_item_mismatch");
    if (reviews.existsByOrderItemId(orderItemId))
      throw new IllegalArgumentException("review.error.duplicate_order_item");
    Product product =
        products.lockById(item.getProductId())
            .orElseThrow(()
                             -> new ResourceNotFoundException(
                                 "review.error.product_not_found"));
    if (!product.getId().equals(item.getProductId()))
      throw new IllegalArgumentException("review.error.product_mismatch");
    Review review = new Review();
    review.setCustomer(customer);
    review.setOrder(order);
    review.setOrderItem(item);
    review.setProduct(product);
    review.setRating(r.rating());
    review.setComment(clean(r.comment()));
    addRating(product, r.rating());
    try {
      return mapper.toResponse(reviews.saveAndFlush(review));
    } catch (DataIntegrityViolationException ex) {
      throw new IllegalArgumentException("review.error.duplicate_order_item");
    }
  }

  @Transactional
  public ReviewResponse update(Long reviewId, UpdateReviewRequest r) {
    User customer = ctx.currentCustomer();
    Review review = reviews.findWithDetailsById(reviewId).orElseThrow(
        () -> new ResourceNotFoundException("review.error.not_found"));
    if (!review.getCustomer().getId().equals(customer.getId()))
      throw new AccessDeniedException("review.error.update_forbidden");
    int old = review.getRating();
    review.setComment(clean(r.comment()));
    if (old != r.rating()) {
      Product product =
          products.lockById(review.getProduct().getId())
              .orElseThrow(()
                               -> new ResourceNotFoundException(
                                   "review.error.product_not_found"));
      replaceRating(product, old, r.rating());
      review.setProduct(product);
      review.setRating(r.rating());
    }
    return mapper.toResponse(review);
  }

  @Transactional(readOnly = true)
  public PaginationResponse<ProductReviewResponse>
  productReviews(Long productId, String sort, Pageable pageable) {
    if (!products.existsById(productId))
      throw new ResourceNotFoundException("review.error.product_not_found");
    return PaginationResponse.from(
        reviews.findByProductId(productId, page(pageable, sort))
            .map(mapper::toProductResponse));
  }

  @Transactional(readOnly = true)
  public RatingSummaryResponse summary(Long productId) {
    Product p = products.findById(productId).orElseThrow(
        () -> new ResourceNotFoundException("review.error.product_not_found"));
    Map<Integer, Long> dist = new LinkedHashMap<>();
    for (int i = 1; i <= 5; i++)
      dist.put(i, 0L);
    reviews.ratingDistribution(productId).forEach(
        rc -> dist.put(rc.getRating(), rc.getTotal()));
    return new RatingSummaryResponse(p.getId(), p.getAverageRating(),
                                     p.getReviewsCount(), dist);
  }

  @Transactional(readOnly = true)
  public PaginationResponse<CustomerReviewResponse> mine(String sort,
                                                         Pageable pageable) {
    return PaginationResponse.from(
        reviews
            .findByCustomerId(ctx.currentCustomer().getId(),
                              page(pageable, sort))
            .map(mapper::toCustomerResponse));
  }

  @Transactional(readOnly = true)
  public PaginationResponse<AdminReviewResponse>
  admin(Long productId, Long customerId, String orderNumber, Integer rating,
        Instant dateFrom, Instant dateTo, String sort, Pageable pageable) {
    if (rating != null && (rating < 1 || rating > 5))
      throw new IllegalArgumentException("review.error.rating_range");
    String n =
        orderNumber == null || orderNumber.isBlank() ? null : orderNumber;
    return PaginationResponse.from(reviews
                                       .searchAdmin(productId, customerId, n,
                                                    rating, dateFrom, dateTo,
                                                    page(pageable, sort))
                                       .map(mapper::toAdminResponse));
  }

  private Pageable page(Pageable p, String sort) {
    return PageRequest.of(
        p.getPageNumber(), p.getPageSize(),
        switch (sort == null ? "newest" : sort) {
          case "oldest" -> Sort.by("createdAt").ascending();
          case "highest_rating" ->
            Sort.by(Sort.Order.desc("rating"), Sort.Order.desc("createdAt"));
          case "lowest_rating" ->
            Sort.by(Sort.Order.asc("rating"), Sort.Order.desc("createdAt"));
          default -> Sort.by("createdAt").descending();
        });
  }
  private String clean(String c) {
    if (c == null)
      return null;
    String t = c.trim();
    if (t.isBlank())
      return null;
    if (t.length() < 2)
      throw new IllegalArgumentException("review.error.invalid_request");
    return t;
  }
  private void addRating(Product p, int rating) {
    p.setRatingSum(nvl(p.getRatingSum()) + rating);
    p.setReviewsCount(nvl(p.getReviewsCount()) + 1);
    avg(p);
  }
  private void replaceRating(Product p, int oldRating, int newRating) {
    p.setRatingSum(nvl(p.getRatingSum()) - oldRating + newRating);
    if (p.getRatingSum() < 0)
      throw new IllegalArgumentException("review.error.invalid_request");
    avg(p);
  }
  private void avg(Product p) {
    int count = nvl(p.getReviewsCount());
    BigDecimal avg =
        count == 0 ? BigDecimal.ZERO
                   : BigDecimal.valueOf(nvl(p.getRatingSum()))
                         .divide(BigDecimal.valueOf(count), SCALE, ROUNDING);
    if (avg.compareTo(BigDecimal.ZERO) < 0 ||
        avg.compareTo(BigDecimal.valueOf(5)) > 0)
      throw new IllegalArgumentException("review.error.invalid_request");
    p.setAverageRating(avg.setScale(SCALE, ROUNDING));
  }
  private int nvl(Integer v) { return v == null ? 0 : v; }
  private long nvl(Long v) { return v == null ? 0L : v; }
}
