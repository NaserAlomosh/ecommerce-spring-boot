package com.smart.ecommerce.dashboard.repository;

import com.smart.ecommerce.entity.Order;
import com.smart.ecommerce.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DashboardOrderRepository extends JpaRepository<Order, Long> {
    @Query("""
            select coalesce(sum(o.totalAmount), 0)
            from Order o
            where o.status = com.smart.ecommerce.enums.OrderStatus.COMPLETED
              and (:fromDate is null or o.createdAt >= :fromDate)
              and (:toDate is null or o.createdAt < :toDate)
            """)
    BigDecimal completedRevenue(@Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);

    @Query("select o.status as status, count(o) as total from Order o group by o.status")
    List<OrderStatusCountProjection> countByStatus();

    @Query("""
            select o.orderNumber as orderNumber,
                   concat(c.firstName, ' ', c.lastName) as customerName,
                   o.totalAmount as totalAmount,
                   o.status as status,
                   o.createdAt as createdAt
            from Order o join o.customer c
            order by o.createdAt desc
            """)
    List<LatestOrderProjection> findLatestOrders(Pageable pageable);

    @Query("""
            select oi.productId as productId,
                   oi.productName as productName,
                   coalesce(sum(oi.quantity), 0) as totalSold,
                   coalesce(sum(oi.lineTotal), 0) as revenue,
                   coalesce(p.averageRating, 0) as averageRating
            from OrderItem oi
            join oi.order o
            left join Product p on p.id = oi.productId
            where o.status in :statuses
            group by oi.productId, oi.productName, p.averageRating
            order by sum(oi.quantity) desc, sum(oi.lineTotal) desc
            """)
    List<TopSellingProductProjection> findTopSellingProducts(@Param("statuses") Collection<OrderStatus> statuses, Pageable pageable);

    interface OrderStatusCountProjection { OrderStatus getStatus(); long getTotal(); }
    interface LatestOrderProjection { String getOrderNumber(); String getCustomerName(); BigDecimal getTotalAmount(); OrderStatus getStatus(); Instant getCreatedAt(); }
    interface TopSellingProductProjection { Long getProductId(); String getProductName(); long getTotalSold(); BigDecimal getRevenue(); BigDecimal getAverageRating(); }
}
