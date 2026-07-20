package com.smart.ecommerce.dto.order;

import com.smart.ecommerce.enums.OrderStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class OrderDtos { private OrderDtos() {}
 public record CreateOrderRequest(@NotNull @Positive Long addressId, @Size(max=1000) String customerNotes) {}
 public record CancelOrderRequest(@NotBlank @Size(max=500) String reason) {}
 public record UpdateOrderStatusRequest(@NotNull OrderStatus status, @Size(max=500) String note) {}
 public record OrderAddressResponse(String recipientName, String phoneNumber, String city, BigDecimal latitude, BigDecimal longitude, String area, String street, String additionalDirections) {}
 public record OrderItemResponse(Long id, Long productId, String productName, String productImageUrl, Integer quantity, BigDecimal unitPrice, String currency, BigDecimal lineTotal) {}
 public record OrderResponse(Long id, String orderNumber, OrderStatus status, String currency, BigDecimal subtotal, BigDecimal deliveryFee, BigDecimal discountAmount, BigDecimal totalAmount, Integer totalItems, String customerNotes, String cancellationReason, Instant createdAt, Instant updatedAt, Instant cancelledAt, Instant completedAt, OrderAddressResponse address, List<OrderItemResponse> items) {}
 public record OrderSummaryResponse(Long id, String orderNumber, OrderStatus status, String currency, BigDecimal totalAmount, Integer totalItems, String city, Instant createdAt) {}
 public record OrderStatusHistoryResponse(OrderStatus previousStatus, OrderStatus newStatus, Long changedByUserId, String changedByRole, String note, Instant changedAt) {}
}
