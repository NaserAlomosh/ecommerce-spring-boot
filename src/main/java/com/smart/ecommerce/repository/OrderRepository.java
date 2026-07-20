package com.smart.ecommerce.repository;

import com.smart.ecommerce.entity.Order;
import com.smart.ecommerce.enums.OrderStatus;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
 boolean existsByOrderNumber(String orderNumber);
 @EntityGraph(attributePaths={"items"}) Optional<Order> findByIdAndCustomerId(Long id, Long customerId);
 @EntityGraph(attributePaths={"items"}) Optional<Order> findByOrderNumberAndCustomerId(String orderNumber, Long customerId);
 @EntityGraph(attributePaths={"items"}) Optional<Order> findById(Long id);
 @EntityGraph(attributePaths={"items"}) Optional<Order> findByOrderNumber(String orderNumber);
 Page<Order> findByCustomerId(Long customerId, Pageable pageable);
 Page<Order> findByCustomerIdAndStatus(Long customerId, OrderStatus status, Pageable pageable);
 Page<Order> findByAssignedDeliveryUserId(Long deliveryUserId, Pageable pageable);
 Page<Order> findByAssignedDeliveryUserIdAndStatus(Long deliveryUserId, OrderStatus status, Pageable pageable);
 @EntityGraph(attributePaths={"items"}) Optional<Order> findByOrderNumberAndAssignedDeliveryUserId(String orderNumber, Long deliveryUserId);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select o from Order o left join fetch o.items where o.id=:id") Optional<Order> lockWithItemsById(@Param("id") Long id);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select o from Order o left join fetch o.items where o.orderNumber=:orderNumber") Optional<Order> lockWithItemsByOrderNumber(@Param("orderNumber") String orderNumber);
 @Query("select o from Order o left join o.customer c where (:status is null or o.status=:status) and (:orderNumber is null or lower(o.orderNumber) like lower(concat('%',:orderNumber,'%'))) and (:customer is null or lower(c.email) like lower(concat('%',:customer,'%')) or lower(c.firstName) like lower(concat('%',:customer,'%')) or lower(c.lastName) like lower(concat('%',:customer,'%'))) and (:fromDate is null or o.createdAt>=:fromDate) and (:toDate is null or o.createdAt<=:toDate)")
 Page<Order> searchAdmin(@Param("status") OrderStatus status, @Param("orderNumber") String orderNumber, @Param("customer") String customer, @Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate, Pageable pageable);
}
