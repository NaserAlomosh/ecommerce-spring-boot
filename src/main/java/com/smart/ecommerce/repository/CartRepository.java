package com.smart.ecommerce.repository;

import com.smart.ecommerce.entity.Cart;
import com.smart.ecommerce.enums.CartStatus;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, Long> {
  @EntityGraph(attributePaths = {"items", "items.product"})
  Optional<Cart> findByCustomerIdAndStatus(Long customerId, CartStatus status);
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select c from Cart c where c.customer.id=:customerId and " +
         "c.status=:status")
  Optional<Cart>
  lockByCustomerIdAndStatus(@Param("customerId") Long customerId,
                            @Param("status") CartStatus status);
}
