package com.smart.ecommerce.dashboard.repository;
import com.smart.ecommerce.entity.Review;import java.math.BigDecimal;import org.springframework.data.jpa.repository.*;
public interface DashboardReviewRepository extends JpaRepository<Review, Long> { @Query("select coalesce(avg(r.rating), 0) from Review r") BigDecimal averageRating(); }
