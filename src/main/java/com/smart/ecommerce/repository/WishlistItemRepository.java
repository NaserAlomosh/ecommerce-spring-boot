package com.smart.ecommerce.repository;

import com.smart.ecommerce.entity.WishlistItem;
import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
 @EntityGraph(attributePaths={"product","product.category","product.images"}) List<WishlistItem> findByCustomerIdOrderByCreatedAtAsc(Long customerId);
 @EntityGraph(attributePaths={"product","product.category","product.images"}) Optional<WishlistItem> findByCustomerIdAndProductId(Long customerId, Long productId);
 boolean existsByCustomerIdAndProductId(Long customerId, Long productId);
 void deleteByCustomerIdAndProductId(Long customerId, Long productId);
}
