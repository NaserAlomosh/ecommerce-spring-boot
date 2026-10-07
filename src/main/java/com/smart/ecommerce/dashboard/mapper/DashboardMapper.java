package com.smart.ecommerce.dashboard.mapper;

import com.smart.ecommerce.dashboard.dto.DashboardResponse.*;
import com.smart.ecommerce.dashboard.repository.DashboardOrderRepository.*;
import com.smart.ecommerce.dashboard.repository.DashboardProductRepository.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class DashboardMapper {
  public LatestOrder latestOrder(LatestOrderProjection p) {
    return new LatestOrder(p.getOrderNumber(), p.getCustomerName(),
                           p.getTotalAmount(), p.getStatus(), p.getCreatedAt());
  }
  public TopSellingProduct topSellingProduct(TopSellingProductProjection p) {
    return new TopSellingProduct(p.getProductId(), p.getProductName(),
                                 p.getTotalSold(), money(p.getRevenue()),
                                 rating(p.getAverageRating()));
  }
  public LowStockProduct lowStockProduct(LowStockProductProjection p) {
    return new LowStockProduct(p.getProductId(), p.getName(), p.getStock());
  }
  public OutOfStockProduct outOfStockProduct(OutOfStockProductProjection p) {
    return new OutOfStockProduct(p.getProductId(), p.getName());
  }
  public BigDecimal money(BigDecimal value) {
    return (value == null ? BigDecimal.ZERO : value)
        .setScale(3, RoundingMode.HALF_UP);
  }
  public BigDecimal rating(BigDecimal value) {
    return (value == null ? BigDecimal.ZERO : value)
        .setScale(2, RoundingMode.HALF_UP);
  }
}
