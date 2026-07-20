package com.smart.ecommerce.repository;

import com.smart.ecommerce.entity.ProductImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    long countByProductId(Long productId);
    List<ProductImage> findByProductIdOrderBySortOrderAsc(Long productId);
}
