package com.smart.ecommerce.report.dto;
import com.smart.ecommerce.enums.OrderStatus;import com.smart.ecommerce.report.util.*;import java.math.BigDecimal;import java.time.*;import java.util.*;
public final class ReportDtos{private ReportDtos(){}
 public record DateRangeResponse(LocalDate dateFrom,LocalDate dateTo,Instant startDateTime,Instant endDateTimeExclusive,String timezone){}
 public record SalesSummaryResponse(LocalDate dateFrom,LocalDate dateTo,String currency,BigDecimal totalRevenue,long completedOrders,long totalItemsSold,BigDecimal averageOrderValue,long uniqueCustomers,BigDecimal averageItemsPerOrder,BigDecimal highestOrderValue,BigDecimal lowestOrderValue,long newCustomersWithCompletedOrders,long returningCustomersWithCompletedOrders){}
 public record Change(BigDecimal amount,BigDecimal percentage,GrowthState growthState){}
 public record SalesComparisonResponse(DateRangeResponse currentPeriod,DateRangeResponse previousPeriod,BigDecimal currentRevenue,BigDecimal previousRevenue,BigDecimal revenueChangeAmount,BigDecimal revenueChangePercentage,GrowthState revenueGrowthState,long currentOrders,long previousOrders,long ordersChangeAmount,BigDecimal ordersChangePercentage,GrowthState ordersGrowthState,long currentItemsSold,long previousItemsSold,BigDecimal itemsChangePercentage,GrowthState itemsGrowthState,BigDecimal currentAverageOrderValue,BigDecimal previousAverageOrderValue,BigDecimal averageOrderValueChangePercentage,GrowthState averageOrderValueGrowthState){}
 public record TrendPoint(String label,Instant periodStart,BigDecimal revenue,long ordersCount,long itemsSold,BigDecimal averageOrderValue){}
 public record SalesTrendResponse(LocalDate dateFrom,LocalDate dateTo,ReportGranularity granularity,List<TrendPoint> points){}
 public record OrderStatusSummaryResponse(long totalOrders,long pending,long processing,long outForDelivery,long completed,long failed,long cancelled,BigDecimal completionRate,BigDecimal cancellationRate,BigDecimal failureRate){}
 public record DeliveryUserSummary(Long id,String name){}
 public record SalesOrderRow(Long orderId,String orderNumber,Long customerId,String customerName,BigDecimal totalAmount,String currency,long itemsCount,long totalQuantity,Instant completedAt,Instant createdAt,DeliveryUserSummary assignedDeliveryUser){}
 public record TopProductResponse(Long productId,String productName,String sku,Long categoryId,String categoryName,long quantitySold,long ordersCount,BigDecimal revenue,BigDecimal averageSellingPrice,int currentStock,BigDecimal averageRating,long reviewsCount){}
 public record CategorySalesResponse(Long categoryId,String categoryName,long productsSold,long quantitySold,long completedOrders,BigDecimal revenue,BigDecimal revenueSharePercentage,BigDecimal averageOrderItemValue,String historicalAccuracyNote){}
 public record CustomerSalesResponse(Long customerId,String customerName,long completedOrders,long itemsPurchased,BigDecimal totalSpent,BigDecimal averageOrderValue,Instant firstCompletedOrderAt,Instant lastCompletedOrderAt,String classification){}
 public record RecentProductOrderItem(String orderNumber,String customerName,Instant completedAt,int quantity,BigDecimal unitPrice,BigDecimal lineTotal,String currency){}
 public record ProductSalesResponse(Long productId,String productName,String sku,Long categoryId,String categoryName,BigDecimal totalRevenue,long totalQuantitySold,long completedOrders,BigDecimal averageSellingPrice,int currentStock,BigDecimal averageRating,long reviewsCount,List<TrendPoint> trendPoints,List<RecentProductOrderItem> recentCompletedOrderItems){}
 public record ExportedReport(String filename,byte[] content,int rowCount){}
}
