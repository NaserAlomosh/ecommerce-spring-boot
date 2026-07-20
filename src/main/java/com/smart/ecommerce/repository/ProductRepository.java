package com.smart.ecommerce.repository;

import com.smart.ecommerce.entity.Product;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @EntityGraph(attributePaths = {"category", "images"})
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findWithImagesById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p left join fetch p.images where p.id = :id")
    Optional<Product> lockWithImagesById(@Param("id") Long id);
}
