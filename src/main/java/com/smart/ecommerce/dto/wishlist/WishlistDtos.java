package com.smart.ecommerce.dto.wishlist;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class WishlistDtos { private WishlistDtos() {}
 public record AddWishlistItemRequest(@NotNull Long productId) {}
 public record WishlistProductResponse(Long id, Long categoryId, String nameEn, String nameAr, String sku, BigDecimal price, BigDecimal discountPrice, BigDecimal effectivePrice, BigDecimal discountPercentage, String primaryImageUrl, boolean active, boolean inStock, int availableStock) {}
 public record WishlistItemResponse(Long id, WishlistProductResponse product, Instant createdAt) {}
 public record WishlistResponse(List<WishlistItemResponse> items, int totalItems) {}
 public record WishlistCheckResponse(boolean wishlisted) {}
}
