package com.smart.ecommerce.dto.cart;

import com.smart.ecommerce.enums.CartStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class CartDtos { private CartDtos() {}
 public record AddCartItemRequest(@NotNull Long productId, @NotNull @Min(1) Integer quantity) {}
 public record UpdateCartItemQuantityRequest(@NotNull @Min(1) Integer quantity) {}
 public record CartProductResponse(Long id, String nameEn, String nameAr, String sku, String primaryImageUrl, boolean active, boolean inStock, int availableStock, java.math.BigDecimal averageRating, Integer reviewsCount) {}
 public record CartItemResponse(Long id, CartProductResponse product, int quantity, BigDecimal unitPrice, String currency, BigDecimal discountPrice, BigDecimal effectivePrice, BigDecimal lineTotal, boolean available, boolean quantityExceedsStock, Instant createdAt, Instant updatedAt) {}
 public record CartResponse(Long id, CartStatus status, List<CartItemResponse> items, int totalItems, int distinctItems, BigDecimal subtotal, String currency) {}
}
