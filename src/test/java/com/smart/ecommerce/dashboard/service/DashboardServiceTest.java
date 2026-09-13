package com.smart.ecommerce.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.smart.ecommerce.dashboard.mapper.DashboardMapper;
import com.smart.ecommerce.dashboard.repository.*;
import com.smart.ecommerce.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {
  @Mock DashboardOrderRepository orders;
  @Mock DashboardProductRepository products;
  @Mock DashboardCategoryRepository categories;
  @Mock DashboardUserRepository users;
  @Mock DashboardReviewRepository reviews;

  @Test
  void overviewUsesCompletedRevenueWindowsAndAggregatesStatusCounts() {
    Clock clock =
        Clock.fixed(Instant.parse("2026-07-20T12:00:00Z"), ZoneOffset.UTC);
    DashboardService service = new DashboardService(
        orders, products, categories, users, reviews,
        new DashboardProperties(10), new DashboardMapper(), clock);
    when(orders.completedRevenue(null, null))
        .thenReturn(new BigDecimal("100.1234"));
    when(orders.completedRevenue(Instant.parse("2026-07-20T00:00:00Z"),
                                 Instant.parse("2026-07-20T12:00:00Z")))
        .thenReturn(new BigDecimal("10"));
    when(orders.completedRevenue(Instant.parse("2026-07-01T00:00:00Z"),
                                 Instant.parse("2026-07-20T12:00:00Z")))
        .thenReturn(new BigDecimal("50"));
    when(orders.countByStatus())
        .thenReturn(List.of(statusCount(OrderStatus.PENDING, 2),
                            statusCount(OrderStatus.COMPLETED, 3)));
    when(products.countProducts()).thenReturn(productCounts(7, 5, 2));
    when(categories.count()).thenReturn(4L);
    when(users.countByRole(any())).thenReturn(9L);
    when(products.countLowStock(10)).thenReturn(2L);
    when(products.countOutOfStock()).thenReturn(1L);
    when(reviews.count()).thenReturn(6L);
    when(reviews.averageRating()).thenReturn(new BigDecimal("4.333"));
    var response = service.overview();
    assertThat(response.sales().totalRevenue()).isEqualByComparingTo("100.123");
    assertThat(response.orders().totalOrders()).isEqualTo(5);
    assertThat(response.orders().pendingOrders()).isEqualTo(2);
    assertThat(response.orders().completedOrders()).isEqualTo(3);
    assertThat(response.reviews().averageRating()).isEqualByComparingTo("4.33");
    verify(products).findLowStock(eq(10), any(Pageable.class));
  }

  private DashboardOrderRepository.OrderStatusCountProjection
  statusCount(OrderStatus s, long total) {
    return new DashboardOrderRepository.OrderStatusCountProjection() {
      public OrderStatus getStatus() { return s; }
      public long getTotal() { return total; }
    };
  }
  private DashboardProductRepository.ProductCountsProjection
  productCounts(long total, long active, long inactive) {
    return new DashboardProductRepository.ProductCountsProjection() {
      public long getTotalProducts() { return total; }
      public long getActiveProducts() { return active; }
      public long getInactiveProducts() { return inactive; }
    };
  }
}
