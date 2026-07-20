package com.smart.ecommerce.repository;

import com.smart.ecommerce.entity.Review;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByOrderItemId(Long orderItemId);

    @EntityGraph(attributePaths = {"customer", "product", "order", "orderItem"})
    @Query("select r from Review r where r.id = :id")
    Optional<Review> findWithDetailsById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"customer"})
    Page<Review> findByProductId(Long productId, Pageable pageable);

    @EntityGraph(attributePaths = {"product", "order", "orderItem"})
    Page<Review> findByCustomerId(Long customerId, Pageable pageable);

    @Query("select r.rating as rating, count(r) as total from Review r where r.product.id = :productId group by r.rating")
    List<RatingCount> ratingDistribution(@Param("productId") Long productId);

    @EntityGraph(attributePaths = {"customer", "product", "order", "orderItem"})
    @Query("select r from Review r join r.order o where (:productId is null or r.product.id=:productId) and (:customerId is null or r.customer.id=:customerId) and (:orderNumber is null or lower(o.orderNumber) like lower(concat('%',:orderNumber,'%'))) and (:rating is null or r.rating=:rating) and (:dateFrom is null or r.createdAt>=:dateFrom) and (:dateTo is null or r.createdAt<=:dateTo)")
    Page<Review> searchAdmin(@Param("productId") Long productId, @Param("customerId") Long customerId, @Param("orderNumber") String orderNumber, @Param("rating") Integer rating, @Param("dateFrom") Instant dateFrom, @Param("dateTo") Instant dateTo, Pageable pageable);

    interface RatingCount { Integer getRating(); Long getTotal(); }
}
