package com.smart.ecommerce.dashboard.service;

import com.smart.ecommerce.dashboard.dto.DashboardResponse;
import com.smart.ecommerce.dashboard.dto.DashboardResponse.*;
import com.smart.ecommerce.dashboard.mapper.DashboardMapper;
import com.smart.ecommerce.dashboard.repository.*;
import com.smart.ecommerce.enums.OrderStatus;
import com.smart.ecommerce.enums.Role;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
  private static final int DASHBOARD_LIST_SIZE = 10;
  private final DashboardOrderRepository orders;
  private final DashboardProductRepository products;
  private final DashboardCategoryRepository categories;
  private final DashboardUserRepository users;
  private final DashboardReviewRepository reviews;
  private final DashboardProperties properties;
  private final DashboardMapper mapper;
  private final Clock clock;

  public DashboardResponse overview() {
    Instant now = Instant.now(clock);
    ZoneId zone = ZoneOffset.UTC;
    Instant todayStart = LocalDate.now(clock).atStartOfDay(zone).toInstant();
    Instant monthStart =
        YearMonth.now(clock).atDay(1).atStartOfDay(zone).toInstant();
    int threshold = properties.lowStockThreshold();
    var pageable = PageRequest.of(0, DASHBOARD_LIST_SIZE);

    Map<OrderStatus, Long> byStatus =
        orders.countByStatus().stream().collect(Collectors.toMap(
            DashboardOrderRepository.OrderStatusCountProjection::getStatus,
            DashboardOrderRepository.OrderStatusCountProjection::getTotal));
    long totalOrders =
        byStatus.values().stream().mapToLong(Long::longValue).sum();
    var productCounts = products.countProducts();

    return new DashboardResponse(
        new SalesSummary(
            mapper.money(orders.completedRevenue(null, null)),
            mapper.money(orders.completedRevenue(todayStart, now)),
            mapper.money(orders.completedRevenue(monthStart, now))),
        new OrderSummary(totalOrders, count(byStatus, OrderStatus.PENDING),
                         count(byStatus, OrderStatus.PROCESSING),
                         count(byStatus, OrderStatus.OUT_FOR_DELIVERY),
                         count(byStatus, OrderStatus.COMPLETED),
                         count(byStatus, OrderStatus.CANCELLED),
                         count(byStatus, OrderStatus.FAILED)),
        new ProductSummary(productCounts.getTotalProducts(),
                           productCounts.getActiveProducts(),
                           productCounts.getInactiveProducts()),
        new CategorySummary(categories.count()),
        new CustomerSummary(users.countByRole(Role.CUSTOMER)),
        new InventorySummary(products.countLowStock(threshold),
                             products.countOutOfStock()),
        new ReviewSummary(reviews.count(),
                          mapper.rating(reviews.averageRating())),
        orders.findLatestOrders(pageable)
            .stream()
            .map(mapper::latestOrder)
            .toList(),
        orders.findTopSellingProducts(List.of(OrderStatus.COMPLETED), pageable)
            .stream()
            .map(mapper::topSellingProduct)
            .toList(),
        products.findLowStock(threshold, pageable)
            .stream()
            .map(mapper::lowStockProduct)
            .toList(),
        products.findOutOfStock(pageable)
            .stream()
            .map(mapper::outOfStockProduct)
            .toList());
  }

  private long count(Map<OrderStatus, Long> counts, OrderStatus status) {
    return counts.getOrDefault(status, 0L);
  }
}
