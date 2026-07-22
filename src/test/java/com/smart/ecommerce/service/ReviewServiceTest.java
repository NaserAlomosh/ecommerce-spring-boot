package com.smart.ecommerce.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.smart.ecommerce.dto.review.ReviewDtos.*;
import com.smart.ecommerce.entity.*;
import com.smart.ecommerce.enums.*;
import com.smart.ecommerce.repository.*;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
  @Mock CustomerContextService ctx;
  @Mock OrderRepository orders;
  @Mock OrderItemRepository items;
  @Mock ProductRepository products;
  @Mock ReviewRepository reviews;
  ReviewMapper mapper = new ReviewMapper();
  ReviewService service;

  @BeforeEach
  void setup() {
    service = new ReviewService(ctx, orders, items, products, reviews, mapper);
  }

  @Test
  void customerCanCreateReviewAndUpdatesAggregates() {
    User c = customer(1L);
    Order o = order(10L, c, OrderStatus.COMPLETED);
    OrderItem i = item(20L, o, 30L);
    Product p = product(30L);
    when(ctx.currentCustomer()).thenReturn(c);
    when(orders.findByOrderNumber("ORD")).thenReturn(Optional.of(o));
    when(items.findById(20L)).thenReturn(Optional.of(i));
    when(products.lockById(30L)).thenReturn(Optional.of(p));
    when(reviews.saveAndFlush(any())).thenAnswer(inv -> {
      Review r = inv.getArgument(0);
      r.setId(99L);
      return r;
    });
    ReviewResponse res =
        service.create("ORD", 20L, new CreateReviewRequest(5, " great "));
    assertThat(res.rating()).isEqualTo(5);
    assertThat(res.comment()).isEqualTo("great");
    assertThat(p.getRatingSum()).isEqualTo(5);
    assertThat(p.getReviewsCount()).isEqualTo(1);
    assertThat(p.getAverageRating()).isEqualByComparingTo("5.00");
  }

  @Test
  void blankCommentStoredAsNullAndRatingOneAccepted() {
    User c = customer(1L);
    Order o = order(10L, c, OrderStatus.COMPLETED);
    OrderItem i = item(20L, o, 30L);
    Product p = product(30L);
    when(ctx.currentCustomer()).thenReturn(c);
    when(orders.findByOrderNumber("ORD")).thenReturn(Optional.of(o));
    when(items.findById(20L)).thenReturn(Optional.of(i));
    when(products.lockById(30L)).thenReturn(Optional.of(p));
    when(reviews.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
    ReviewResponse res =
        service.create("ORD", 20L, new CreateReviewRequest(1, "   "));
    assertThat(res.comment()).isNull();
    assertThat(p.getAverageRating()).isEqualByComparingTo("1.00");
  }

  @Test
  void rejectsNonCompletedOrderWithoutChangingAggregates() {
    User c = customer(1L);
    Order o = order(10L, c, OrderStatus.PROCESSING);
    when(ctx.currentCustomer()).thenReturn(c);
    when(orders.findByOrderNumber("ORD")).thenReturn(Optional.of(o));
    assertThatThrownBy(
        () -> service.create("ORD", 20L, new CreateReviewRequest(4, null)))
        .hasMessage("review.error.completed_only");
    verify(products, never()).lockById(anyLong());
    verify(reviews, never()).saveAndFlush(any());
  }

  @Test
  void duplicateReviewRejectedBeforeAggregateUpdate() {
    User c = customer(1L);
    Order o = order(10L, c, OrderStatus.COMPLETED);
    OrderItem i = item(20L, o, 30L);
    when(ctx.currentCustomer()).thenReturn(c);
    when(orders.findByOrderNumber("ORD")).thenReturn(Optional.of(o));
    when(items.findById(20L)).thenReturn(Optional.of(i));
    when(reviews.existsByOrderItemId(20L)).thenReturn(true);
    assertThatThrownBy(
        () -> service.create("ORD", 20L, new CreateReviewRequest(4, null)))
        .hasMessage("review.error.duplicate_order_item");
    verify(products, never()).lockById(anyLong());
  }

  @Test
  void updateRatingUsesDeltaAndKeepsReviewCount() {
    User c = customer(1L);
    Product p = product(30L);
    p.setRatingSum(9L);
    p.setReviewsCount(2);
    p.setAverageRating(new BigDecimal("4.50"));
    Review r = review(40L, c, p, 5);
    when(ctx.currentCustomer()).thenReturn(c);
    when(reviews.findWithDetailsById(40L)).thenReturn(Optional.of(r));
    when(products.lockById(30L)).thenReturn(Optional.of(p));
    service.update(40L, new UpdateReviewRequest(2, "ok"));
    assertThat(p.getRatingSum()).isEqualTo(6);
    assertThat(p.getReviewsCount()).isEqualTo(2);
    assertThat(p.getAverageRating()).isEqualByComparingTo("3.00");
    assertThat(r.getRating()).isEqualTo(2);
  }

  private User customer(Long id) {
    User u = new User();
    u.setId(id);
    u.setRole(Role.CUSTOMER);
    u.setFirstName("Naser");
    u.setLastName("Alomosh");
    return u;
  }
  private Product product(Long id) {
    Product p = new Product();
    p.setId(id);
    p.setNameEn("P");
    p.setNameAr("P");
    p.setRatingSum(0L);
    p.setReviewsCount(0);
    p.setAverageRating(new BigDecimal("0.00"));
    return p;
  }
  private Order order(Long id, User c, OrderStatus s) {
    Order o = new Order();
    o.setId(id);
    o.setCustomer(c);
    o.setStatus(s);
    o.setOrderNumber("ORD");
    return o;
  }
  private OrderItem item(Long id, Order o, Long productId) {
    OrderItem i = new OrderItem();
    i.setId(id);
    i.setOrder(o);
    i.setProductId(productId);
    return i;
  }
  private Review review(Long id, User c, Product p, int rating) {
    Review r = new Review();
    r.setId(id);
    r.setCustomer(c);
    r.setProduct(p);
    r.setRating(rating);
    return r;
  }
}
