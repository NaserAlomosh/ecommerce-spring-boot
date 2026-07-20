package com.smart.ecommerce.dashboard.dto;

import com.smart.ecommerce.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record DashboardResponse(
        SalesSummary sales,
        OrderSummary orders,
        ProductSummary products,
        CategorySummary categories,
        CustomerSummary customers,
        InventorySummary inventory,
        ReviewSummary reviews,
        List<LatestOrder> latestOrders,
        List<TopSellingProduct> topSellingProducts,
        List<LowStockProduct> lowStockProducts,
        List<OutOfStockProduct> outOfStockProducts) {
    public record SalesSummary(BigDecimal totalRevenue, BigDecimal todayRevenue, BigDecimal thisMonthRevenue) {}
    public record OrderSummary(long totalOrders, long pendingOrders, long processingOrders, long outForDeliveryOrders, long completedOrders, long cancelledOrders, long failedOrders) {}
    public record ProductSummary(long totalProducts, long activeProducts, long inactiveProducts) {}
    public record CategorySummary(long totalCategories) {}
    public record CustomerSummary(long totalCustomers) {}
    public record InventorySummary(long lowStockProducts, long outOfStockProducts) {}
    public record ReviewSummary(long totalReviews, BigDecimal averageRating) {}
    public record LatestOrder(String orderNumber, String customerName, BigDecimal totalAmount, OrderStatus status, Instant createdAt) {}
    public record TopSellingProduct(Long productId, String productName, long totalSold, BigDecimal revenue, BigDecimal averageRating) {}
    public record LowStockProduct(Long productId, String name, int stock) {}
    public record OutOfStockProduct(Long productId, String name) {}
}
