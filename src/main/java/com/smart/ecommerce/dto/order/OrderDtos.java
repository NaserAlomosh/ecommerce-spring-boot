package com.smart.ecommerce.dto.order;

import com.smart.ecommerce.enums.DeliveryFailureReason;
import com.smart.ecommerce.enums.OrderStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class OrderDtos { private OrderDtos() {}
 public record CreateOrderRequest(@NotNull @Positive Long addressId, @Size(max=1000) String customerNote) {}
 public record CancelOrderRequest(@NotBlank @Size(max=500) String reason) {}
 public record AssignDeliveryRequest(@NotNull @Positive Long deliveryUserId) {}
 public record UpdateOrderStatusRequest(@NotNull OrderStatus status, @Size(max=500) String note, DeliveryFailureReason failureReason, @Size(max=500) String failureNote) {}
 public record OrderUserResponse(String fullName, String email, String phoneNumber) {}
 public record OrderAddressResponse(String recipientName, String phoneNumber, String city, BigDecimal latitude, BigDecimal longitude, String area, String street, String additionalDirections) {}
 public record OrderItemResponse(Long productId, String productName, String productImageUrl, Integer quantity, BigDecimal unitPrice, String currency, BigDecimal totalPrice) {}
 public record OrderResponse(String orderNumber, OrderStatus status, String statusDescriptionKey, String currency, BigDecimal subtotal, BigDecimal totalAmount, Integer totalItems, String customerNote, DeliveryFailureReason failureReason, String failureReasonDescriptionKey, String failureNote, Instant createdAt, Instant updatedAt, Instant cancelledAt, Instant completedAt, OrderUserResponse customer, OrderUserResponse assignedDeliveryUser, OrderAddressResponse address, List<OrderItemResponse> items) {}
 public record OrderSummaryResponse(String orderNumber, OrderStatus status, String statusDescriptionKey, String currency, BigDecimal totalAmount, Integer totalItems, String city, Instant createdAt) {}
 public record OrderStatusHistoryResponse(OrderStatus previousStatus, OrderStatus newStatus, Long changedByUserId, String changedByRole, String note, DeliveryFailureReason failureReason, Instant changedAt) {}
}
