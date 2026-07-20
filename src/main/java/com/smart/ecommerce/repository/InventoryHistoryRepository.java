package com.smart.ecommerce.repository;

import com.smart.ecommerce.entity.InventoryHistory;
import com.smart.ecommerce.enums.InventoryMovementType;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long> {
 @EntityGraph(attributePaths={"product","order","performedBy"})
 @Query("select h from InventoryHistory h left join h.product p where (:productId is null or p.id=:productId) and (:type is null or h.movementType=:type) and (:customerId is null or h.customerId=:customerId) and (:orderNumber is null or lower(h.orderNumber) like lower(concat('%',:orderNumber,'%'))) and (:fromDate is null or h.createdAt>=:fromDate) and (:toDate is null or h.createdAt<=:toDate)")
 Page<InventoryHistory> search(@Param("productId") Long productId, @Param("type") InventoryMovementType type, @Param("customerId") Long customerId, @Param("orderNumber") String orderNumber, @Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate, Pageable pageable);
 @EntityGraph(attributePaths={"product","order","performedBy"})
 List<InventoryHistory> findByProductIdOrderByCreatedAtDesc(Long productId);
 boolean existsByOrderIdAndProductIdAndMovementType(Long orderId, Long productId, InventoryMovementType movementType);
}
