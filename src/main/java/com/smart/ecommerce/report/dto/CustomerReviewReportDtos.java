package com.smart.ecommerce.report.dto;
import com.smart.ecommerce.enums.*;import com.smart.ecommerce.report.util.*;import java.math.BigDecimal;import java.time.*;import java.util.*;
public final class CustomerReviewReportDtos{private CustomerReviewReportDtos(){}
 public record CustomerSummaryResponse(long totalCustomers,long activeCustomers,long inactiveCustomers,long customersWithOrders,long customersWithoutOrders,long newCustomersToday,long newCustomersThisMonth,BigDecimal averageOrdersPerCustomer,BigDecimal averageSpentPerCustomer,BigDecimal totalRevenueGeneratedByCustomers){}
 public record TopCustomerResponse(Long customerId,String fullName,long completedOrders,BigDecimal totalSpent,BigDecimal averageOrderValue,long totalPurchasedItems,Instant lastOrderDate){}
 public record CustomerHistorySummary(Long customerId,String customerName,long totalOrders,long completedOrders,long cancelledOrders,BigDecimal totalSpent,Instant firstOrderDate,Instant lastOrderDate){}
 public record CustomerHistoryOrder(String orderNumber,OrderStatus status,BigDecimal totalAmount,Instant createdAt,Instant completedAt,long itemsCount){}
 public record CustomerPurchaseHistoryResponse(CustomerHistorySummary summary,com.smart.ecommerce.dto.PaginationResponse<CustomerHistoryOrder> orders){}
 public record CustomerTrendResponse(LocalDate dateFrom,LocalDate dateTo,ReportGranularity granularity,List<CustomerTrendPoint> points){}
 public record CustomerTrendPoint(String label,Instant periodStart,long newCustomers){}
 public record ReviewsSummaryResponse(long totalReviews,BigDecimal averageRating,long totalProductsReviewed,long totalCustomersReviewed,RatingDistribution ratingDistribution){}
 public record RatingDistribution(long fiveStars,long fourStars,long threeStars,long twoStars,long oneStar){}
 public record ReviewTrendResponse(LocalDate dateFrom,LocalDate dateTo,ReportGranularity granularity,List<ReviewTrendPoint> points){}
 public record ReviewTrendPoint(String label,Instant periodStart,long reviewsCount,BigDecimal averageRating){}
 public record RatedProductResponse(Long productId,String productName,BigDecimal averageRating,long totalReviews,int currentStock){}
 public record ProductNoReviewsResponse(Long productId,String productName,int stock,Instant createdAt){}
 public record MostReviewedProductResponse(Long productId,String productName,long reviewsCount,BigDecimal averageRating){}
}
