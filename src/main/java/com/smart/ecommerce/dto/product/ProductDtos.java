package com.smart.ecommerce.dto.product;

import com.smart.ecommerce.dto.category.CategoryDtos.CategorySummary;
import com.smart.ecommerce.enums.CurrencyCode;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public final class ProductDtos {
    private ProductDtos() {}

    public record ProductCreateRequest(
            @NotNull Long categoryId,
            @NotBlank String nameEn,
            @NotBlank String nameAr,
            String descriptionEn,
            String descriptionAr,
            @NotBlank String sku,
            @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
            @Size(min = 3, max = 3) String currency,
            @DecimalMin(value = "0.0", inclusive = false) BigDecimal discountPrice,
            @Min(0) int stockQuantity,
            @Min(0) int lowStockThreshold,
            boolean active,
            boolean featured) {
        public ProductCreateRequest {
            currency = CurrencyCode.normalizeNullable(currency);
        }
    }

    public record ProductUpdateRequest(
            @NotNull Long categoryId,
            @NotBlank String nameEn,
            @NotBlank String nameAr,
            String descriptionEn,
            String descriptionAr,
            @NotBlank String sku,
            @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
            @Size(min = 3, max = 3) String currency,
            @DecimalMin(value = "0.0", inclusive = false) BigDecimal discountPrice,
            @Min(0) int stockQuantity,
            @Min(0) int lowStockThreshold,
            boolean active,
            boolean featured) {
        public ProductUpdateRequest {
            currency = CurrencyCode.normalizeNullable(currency);
        }
    }

    public record ProductImageResponse(Long id, String imageUrl, boolean primaryImage, int sortOrder) {}

    public record ProductResponse(
            Long id, CategorySummary category, String nameEn, String nameAr, String sku, BigDecimal price,
            String currency, BigDecimal discountPrice, BigDecimal effectivePrice, BigDecimal discountPercentage,
            int stockQuantity, int lowStockThreshold, boolean inStock, boolean lowStock,
            boolean active, boolean featured, BigDecimal averageRating, Integer reviewsCount, List<ProductImageResponse> images) {}

    public record ImageOrderRequest(@NotEmpty List<@NotNull Long> imageIds) {}
}
