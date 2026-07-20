package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.*;
import com.smart.ecommerce.dto.review.ReviewDtos.*;
import com.smart.ecommerce.service.ReviewService;
import com.smart.ecommerce.util.MessageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController {
    private final ReviewService service;
    private final MessageUtil messages;

    @Operation(summary = "Create a review for a purchased completed order item", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/customer/orders/{orderNumber}/items/{orderItemId}/review")
    public ApiResponse<ReviewResponse> create(@PathVariable String orderNumber, @PathVariable Long orderItemId, @Valid @RequestBody CreateReviewRequest request) {
        return ApiResponse.success(messages.getMessage("review.created"), service.create(orderNumber, orderItemId, request));
    }

    @Operation(summary = "Update the authenticated customer's review", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/reviews/{reviewId}")
    public ApiResponse<ReviewResponse> update(@PathVariable Long reviewId, @Valid @RequestBody UpdateReviewRequest request) {
        return ApiResponse.success(messages.getMessage("review.updated"), service.update(reviewId, request));
    }

    @Operation(summary = "List public product reviews with sorting: newest, oldest, highest_rating, lowest_rating")
    @GetMapping("/products/{productId}/reviews")
    public ApiResponse<PaginationResponse<ProductReviewResponse>> productReviews(@PathVariable Long productId, @RequestParam(defaultValue = "newest") String sort, Pageable pageable) {
        return ApiResponse.success(messages.getMessage("review.list"), service.productReviews(productId, sort, pageable));
    }

    @Operation(summary = "Get product rating summary and rating distribution")
    @GetMapping("/products/{productId}/rating-summary")
    public ApiResponse<RatingSummaryResponse> summary(@PathVariable Long productId) {
        return ApiResponse.success(messages.getMessage("review.summary"), service.summary(productId));
    }

    @Operation(summary = "List reviews owned by the authenticated customer", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/customers/me/reviews")
    public ApiResponse<PaginationResponse<CustomerReviewResponse>> mine(@RequestParam(defaultValue = "newest") String sort, Pageable pageable) {
        return ApiResponse.success(messages.getMessage("review.mine"), service.mine(sort, pageable));
    }

    @Operation(summary = "Admin review search with filters", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/admin/reviews")
    public ApiResponse<PaginationResponse<AdminReviewResponse>> admin(@RequestParam(required = false) Long productId, @RequestParam(required = false) Long customerId, @RequestParam(required = false) String orderNumber, @RequestParam(required = false) Integer rating, @RequestParam(required = false) Instant dateFrom, @RequestParam(required = false) Instant dateTo, @RequestParam(defaultValue = "newest") String sort, Pageable pageable) {
        return ApiResponse.success(messages.getMessage("review.admin.list"), service.admin(productId, customerId, orderNumber, rating, dateFrom, dateTo, sort, pageable));
    }
}
