package com.smart.ecommerce.report.repository;

import com.smart.ecommerce.entity.Order;
import com.smart.ecommerce.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalesReportRepository extends JpaRepository<Order, Long> {

 @Query("""
        SELECT
            COALESCE(SUM(o.totalAmount), 0) AS totalRevenue,
            COUNT(o.id) AS completedOrders,
            COALESCE(SUM(o.totalItems), 0) AS totalItemsSold,
            COUNT(DISTINCT o.customer.id) AS uniqueCustomers,
            COALESCE(MAX(o.totalAmount), 0) AS highestOrderValue,
            COALESCE(MIN(o.totalAmount), 0) AS lowestOrderValue
        FROM Order o
        WHERE o.status = com.smart.ecommerce.enums.OrderStatus.COMPLETED
          AND COALESCE(o.completedAt, o.createdAt) >= :from
          AND COALESCE(o.completedAt, o.createdAt) < :to
          AND (:currency IS NULL OR o.currency = :currency)
        """)
 SummaryProjection summary(
         @Param("from") Instant from,
         @Param("to") Instant to,
         @Param("currency") String currency
 );

 @Query("""
        SELECT COUNT(DISTINCT o.customer.id)
        FROM Order o
        WHERE o.status = com.smart.ecommerce.enums.OrderStatus.COMPLETED
          AND COALESCE(o.completedAt, o.createdAt) >= :from
          AND COALESCE(o.completedAt, o.createdAt) < :to
          AND NOT EXISTS (
              SELECT p.id
              FROM Order p
              WHERE p.customer.id = o.customer.id
                AND p.status = com.smart.ecommerce.enums.OrderStatus.COMPLETED
                AND COALESCE(p.completedAt, p.createdAt) < :from
          )
        """)
 long newCustomers(
         @Param("from") Instant from,
         @Param("to") Instant to
 );

 @Query("""
        SELECT
            o.status AS status,
            COUNT(o.id) AS total
        FROM Order o
        WHERE o.createdAt >= :from
          AND o.createdAt < :to
        GROUP BY o.status
        """)
 List<StatusProjection> statusSummary(
         @Param("from") Instant from,
         @Param("to") Instant to
 );

 @Query(
         value = """
            SELECT
                o.id AS orderId,
                o.orderNumber AS orderNumber,
                c.id AS customerId,
                CONCAT(c.firstName, ' ', c.lastName) AS customerName,
                o.totalAmount AS totalAmount,
                o.currency AS currency,
                COUNT(oi.id) AS itemsCount,
                COALESCE(SUM(oi.quantity), 0) AS totalQuantity,
                COALESCE(o.completedAt, o.createdAt) AS completedAt,
                o.createdAt AS createdAt,
                d.id AS deliveryUserId,
                CONCAT(d.firstName, ' ', d.lastName) AS deliveryUserName
            FROM Order o
            JOIN o.customer c
            LEFT JOIN o.assignedDeliveryUser d
            JOIN o.items oi
            LEFT JOIN Product p ON p.id = oi.productId
            WHERE o.status = com.smart.ecommerce.enums.OrderStatus.COMPLETED
              AND COALESCE(o.completedAt, o.createdAt) >= :from
              AND COALESCE(o.completedAt, o.createdAt) < :to
              AND (
                  :orderNumber IS NULL
                  OR LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :orderNumber, '%'))
              )
              AND (:customerId IS NULL OR c.id = :customerId)
              AND (
                  :customerName IS NULL
                  OR LOWER(CONCAT(c.firstName, ' ', c.lastName))
                     LIKE LOWER(CONCAT('%', :customerName, '%'))
              )
              AND (:minAmount IS NULL OR o.totalAmount >= :minAmount)
              AND (:maxAmount IS NULL OR o.totalAmount <= :maxAmount)
              AND (:productId IS NULL OR oi.productId = :productId)
              AND (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:deliveryId IS NULL OR d.id = :deliveryId)
              AND (:currency IS NULL OR o.currency = :currency)
            GROUP BY
                o.id,
                o.orderNumber,
                c.id,
                c.firstName,
                c.lastName,
                o.totalAmount,
                o.currency,
                o.completedAt,
                o.createdAt,
                d.id,
                d.firstName,
                d.lastName
            """,
         countQuery = """
            SELECT COUNT(DISTINCT o.id)
            FROM Order o
            JOIN o.customer c
            LEFT JOIN o.assignedDeliveryUser d
            JOIN o.items oi
            LEFT JOIN Product p ON p.id = oi.productId
            WHERE o.status = com.smart.ecommerce.enums.OrderStatus.COMPLETED
              AND COALESCE(o.completedAt, o.createdAt) >= :from
              AND COALESCE(o.completedAt, o.createdAt) < :to
              AND (
                  :orderNumber IS NULL
                  OR LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :orderNumber, '%'))
              )
              AND (:customerId IS NULL OR c.id = :customerId)
              AND (
                  :customerName IS NULL
                  OR LOWER(CONCAT(c.firstName, ' ', c.lastName))
                     LIKE LOWER(CONCAT('%', :customerName, '%'))
              )
              AND (:minAmount IS NULL OR o.totalAmount >= :minAmount)
              AND (:maxAmount IS NULL OR o.totalAmount <= :maxAmount)
              AND (:productId IS NULL OR oi.productId = :productId)
              AND (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:deliveryId IS NULL OR d.id = :deliveryId)
              AND (:currency IS NULL OR o.currency = :currency)
            """
 )
 Page<OrderRowProjection> orderRows(
         @Param("from") Instant from,
         @Param("to") Instant to,
         @Param("orderNumber") String orderNumber,
         @Param("customerId") Long customerId,
         @Param("customerName") String customerName,
         @Param("minAmount") BigDecimal minAmount,
         @Param("maxAmount") BigDecimal maxAmount,
         @Param("productId") Long productId,
         @Param("categoryId") Long categoryId,
         @Param("deliveryId") Long deliveryId,
         @Param("currency") String currency,
         Pageable pageable
 );

 @Query("""
        SELECT
            oi.productId AS productId,
            oi.productName AS productName,
            p.sku AS sku,
            p.category.id AS categoryId,
            COALESCE(p.category.nameEn, 'Historical/Deleted') AS categoryName,
            COALESCE(SUM(oi.quantity), 0) AS quantitySold,
            COUNT(DISTINCT o.id) AS ordersCount,
            COALESCE(SUM(oi.lineTotal), 0) AS revenue,
            COALESCE(p.stockQuantity, 0) AS currentStock,
            COALESCE(p.averageRating, 0) AS averageRating,
            COALESCE(p.reviewsCount, 0) AS reviewsCount
        FROM OrderItem oi
        JOIN oi.order o
        LEFT JOIN Product p ON p.id = oi.productId
        WHERE o.status = com.smart.ecommerce.enums.OrderStatus.COMPLETED
          AND COALESCE(o.completedAt, o.createdAt) >= :from
          AND COALESCE(o.completedAt, o.createdAt) < :to
        GROUP BY
            oi.productId,
            oi.productName,
            p.sku,
            p.category.id,
            p.category.nameEn,
            p.stockQuantity,
            p.averageRating,
            p.reviewsCount
        ORDER BY SUM(oi.lineTotal) DESC
        """)
 List<TopProductProjection> topProducts(
         @Param("from") Instant from,
         @Param("to") Instant to,
         Pageable pageable
 );

 @Query(
         value = """
            SELECT
                p.category.id AS categoryId,
                COALESCE(p.category.nameEn, 'Historical/Deleted') AS categoryName,
                COUNT(DISTINCT oi.productId) AS productsSold,
                COALESCE(SUM(oi.quantity), 0) AS quantitySold,
                COUNT(DISTINCT o.id) AS completedOrders,
                COALESCE(SUM(oi.lineTotal), 0) AS revenue,
                COUNT(oi.id) AS itemRows
            FROM OrderItem oi
            JOIN oi.order o
            LEFT JOIN Product p ON p.id = oi.productId
            WHERE o.status = com.smart.ecommerce.enums.OrderStatus.COMPLETED
              AND COALESCE(o.completedAt, o.createdAt) >= :from
              AND COALESCE(o.completedAt, o.createdAt) < :to
            GROUP BY
                p.category.id,
                p.category.nameEn
            """,
         countQuery = """
            SELECT COUNT(DISTINCT p.category.id)
            FROM OrderItem oi
            JOIN oi.order o
            LEFT JOIN Product p ON p.id = oi.productId
            WHERE o.status = com.smart.ecommerce.enums.OrderStatus.COMPLETED
              AND COALESCE(o.completedAt, o.createdAt) >= :from
              AND COALESCE(o.completedAt, o.createdAt) < :to
            """
 )
 Page<CategoryProjection> categories(
         @Param("from") Instant from,
         @Param("to") Instant to,
         Pageable pageable
 );

 @Query(
         value = """
            SELECT
                c.id AS customerId,
                CONCAT(c.firstName, ' ', c.lastName) AS customerName,
                COUNT(o.id) AS completedOrders,
                COALESCE(SUM(o.totalItems), 0) AS itemsPurchased,
                COALESCE(SUM(o.totalAmount), 0) AS totalSpent,
                MIN(COALESCE(o.completedAt, o.createdAt)) AS firstCompletedOrderAt,
                MAX(COALESCE(o.completedAt, o.createdAt)) AS lastCompletedOrderAt
            FROM Order o
            JOIN o.customer c
            WHERE o.status = com.smart.ecommerce.enums.OrderStatus.COMPLETED
              AND COALESCE(o.completedAt, o.createdAt) >= :from
              AND COALESCE(o.completedAt, o.createdAt) < :to
              AND (:customerId IS NULL OR c.id = :customerId)
              AND (
                  :customerName IS NULL
                  OR LOWER(CONCAT(c.firstName, ' ', c.lastName))
                     LIKE LOWER(CONCAT('%', :customerName, '%'))
              )
            GROUP BY
                c.id,
                c.firstName,
                c.lastName
            """,
         countQuery = """
            SELECT COUNT(DISTINCT c.id)
            FROM Order o
            JOIN o.customer c
            WHERE o.status = com.smart.ecommerce.enums.OrderStatus.COMPLETED
              AND COALESCE(o.completedAt, o.createdAt) >= :from
              AND COALESCE(o.completedAt, o.createdAt) < :to
              AND (:customerId IS NULL OR c.id = :customerId)
              AND (
                  :customerName IS NULL
                  OR LOWER(CONCAT(c.firstName, ' ', c.lastName))
                     LIKE LOWER(CONCAT('%', :customerName, '%'))
              )
            """
 )
 Page<CustomerProjection> customers(
         @Param("from") Instant from,
         @Param("to") Instant to,
         @Param("customerId") Long customerId,
         @Param("customerName") String customerName,
         Pageable pageable
 );

 boolean existsById(Long id);

 @Query("""
        SELECT
            p.id AS productId,
            p.nameEn AS productName,
            p.sku AS sku,
            p.category.id AS categoryId,
            p.category.nameEn AS categoryName,
            p.stockQuantity AS currentStock,
            p.averageRating AS averageRating,
            p.reviewsCount AS reviewsCount
        FROM Product p
        WHERE p.id = :id
        """)
 ProductInfoProjection productInfo(@Param("id") Long id);

 @Query("""
        SELECT
            o.orderNumber AS orderNumber,
            CONCAT(c.firstName, ' ', c.lastName) AS customerName,
            COALESCE(o.completedAt, o.createdAt) AS completedAt,
            oi.quantity AS quantity,
            oi.unitPrice AS unitPrice,
            oi.lineTotal AS lineTotal,
            oi.currency AS currency
        FROM OrderItem oi
        JOIN oi.order o
        JOIN o.customer c
        WHERE o.status = com.smart.ecommerce.enums.OrderStatus.COMPLETED
          AND oi.productId = :productId
          AND COALESCE(o.completedAt, o.createdAt) >= :from
          AND COALESCE(o.completedAt, o.createdAt) < :to
        ORDER BY COALESCE(o.completedAt, o.createdAt) DESC
        """)
 List<RecentItemProjection> recentItems(
         @Param("productId") Long productId,
         @Param("from") Instant from,
         @Param("to") Instant to,
         Pageable pageable
 );

 interface SummaryProjection {
  BigDecimal getTotalRevenue();

  Long getCompletedOrders();

  Long getTotalItemsSold();

  Long getUniqueCustomers();

  BigDecimal getHighestOrderValue();

  BigDecimal getLowestOrderValue();
 }

 interface StatusProjection {
  OrderStatus getStatus();

  Long getTotal();
 }

 interface OrderRowProjection {
  Long getOrderId();

  String getOrderNumber();

  Long getCustomerId();

  String getCustomerName();

  BigDecimal getTotalAmount();

  String getCurrency();

  Long getItemsCount();

  Long getTotalQuantity();

  Instant getCompletedAt();

  Instant getCreatedAt();

  Long getDeliveryUserId();

  String getDeliveryUserName();
 }

 interface TopProductProjection {
  Long getProductId();

  String getProductName();

  String getSku();

  Long getCategoryId();

  String getCategoryName();

  Long getQuantitySold();

  Long getOrdersCount();

  BigDecimal getRevenue();

  Integer getCurrentStock();

  BigDecimal getAverageRating();

  Long getReviewsCount();
 }

 interface CategoryProjection {
  Long getCategoryId();

  String getCategoryName();

  Long getProductsSold();

  Long getQuantitySold();

  Long getCompletedOrders();

  BigDecimal getRevenue();

  Long getItemRows();
 }

 interface CustomerProjection {
  Long getCustomerId();

  String getCustomerName();

  Long getCompletedOrders();

  Long getItemsPurchased();

  BigDecimal getTotalSpent();

  Instant getFirstCompletedOrderAt();

  Instant getLastCompletedOrderAt();
 }

 interface ProductInfoProjection {
  Long getProductId();

  String getProductName();

  String getSku();

  Long getCategoryId();

  String getCategoryName();

  Integer getCurrentStock();

  BigDecimal getAverageRating();

  Long getReviewsCount();
 }

 interface RecentItemProjection {
  String getOrderNumber();

  String getCustomerName();

  Instant getCompletedAt();

  Integer getQuantity();

  BigDecimal getUnitPrice();

  BigDecimal getLineTotal();

  String getCurrency();
 }
}