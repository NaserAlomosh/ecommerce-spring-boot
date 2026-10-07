package com.smart.ecommerce.repository;

import com.smart.ecommerce.entity.CartItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  @EntityGraph(attributePaths = {"cart", "cart.customer", "product"})
  Optional<CartItem> findById(Long id);
  Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
  @Modifying
  @Query("delete from CartItem ci where ci.cart.id=:cartId")
  void deleteByCartId(@Param("cartId") Long cartId);
}
