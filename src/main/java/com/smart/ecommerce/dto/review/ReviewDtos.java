package com.smart.ecommerce.dto.review;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public final class ReviewDtos {
    private ReviewDtos() {}

    public record CreateReviewRequest(
            @NotNull @Min(value = 1, message = "review.error.rating_range") @Max(value = 5, message = "review.error.rating_range") Integer rating,
            @Size(max = 1000, message = "review.error.comment_too_long") String comment) {}

    public record UpdateReviewRequest(
            @NotNull @Min(value = 1, message = "review.error.rating_range") @Max(value = 5, message = "review.error.rating_range") Integer rating,
            @Size(max = 1000, message = "review.error.comment_too_long") String comment) {}

    public record ReviewResponse(Long id, Integer rating, String comment, Instant createdAt, Instant updatedAt) {}

    public record ProductReviewResponse(Long id, Integer rating, String comment, String customerDisplayName, Instant createdAt, Instant updatedAt) {}

    public record ProductSummary(Long id, String nameEn, String nameAr, String imageUrl, BigDecimal averageRating, Integer reviewsCount) {}

    public record CustomerReviewResponse(Long id, Integer rating, String comment, ProductSummary product, String orderNumber, Long orderItemId, Instant createdAt, Instant updatedAt) {}

    public record CustomerSummary(Long id, String displayName) {}

    public record AdminReviewResponse(Long id, Integer rating, String comment, ProductSummary product, CustomerSummary customer, String orderNumber, Long orderItemId, Instant createdAt, Instant updatedAt) {}

    public record RatingSummaryResponse(Long productId, BigDecimal averageRating, Integer reviewsCount, Map<Integer, Long> ratingDistribution) {}
}
