package com.smart.ecommerce.dto.order;

import com.smart.ecommerce.enums.DeliveryFailureReason;
import com.smart.ecommerce.enums.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class OrderDtos {
  private OrderDtos() {}
  public record CreateOrderRequest(@NotNull @Positive Long addressId,
                                   @Size(max = 1000) String customerNote) {}
  public record PublicOrderItemRequest(@NotNull @Positive Long productId,
                                       @NotNull @Min(1) @Max(999)
                                       Integer quantity) {}
  public record PublicOrderLocationRequest(
      @NotBlank @Size(max = 100) String city,
      @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
      @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
      @Size(max = 100) String area, @Size(max = 255) String street,
      @Size(max = 500) String additionalDirections) {}
  public record PublicOrderRequest(
      @NotBlank @Size(max = 150) String name,
      @NotBlank @Pattern(regexp = "^\\+?[0-9]{8,15}$") String phoneNumber,
      @NotNull @Valid PublicOrderLocationRequest location,
      @NotEmpty @Size(max = 100) List<@Valid PublicOrderItemRequest> products,
      @Size(max = 1000) String customerNote) {}
  public record CancelOrderRequest(@NotBlank @Size(max = 500) String reason) {}
  public record AssignDeliveryRequest(@NotNull @Positive Long deliveryUserId) {}
  public record UpdateOrderStatusRequest(@NotNull OrderStatus status,
                                         @Size(max = 500) String note,
                                         DeliveryFailureReason failureReason,
                                         @Size(max = 500) String failureNote) {}
  public record OrderUserResponse(String fullName, String email,
                                  String phoneNumber) {}
  public record OrderAddressResponse(String recipientName, String phoneNumber,
                                     String city, BigDecimal latitude,
                                     BigDecimal longitude, String area,
                                     String street,
                                     String additionalDirections) {}
  public record OrderItemResponse(Long productId, String productName,
                                  String productImageUrl, Integer quantity,
                                  BigDecimal unitPrice, String currency,
                                  BigDecimal totalPrice) {}
  public record
  OrderResponse(String orderNumber, OrderStatus status,
                String statusDescriptionKey, String currency,
                BigDecimal subtotal, BigDecimal totalAmount, Integer totalItems,
                String customerNote, DeliveryFailureReason failureReason,
                String failureReasonDescriptionKey, String failureNote,
                Instant createdAt, Instant updatedAt, Instant cancelledAt,
                Instant completedAt, boolean guestOrder,
                String guestLinkSlug, OrderUserResponse customer,
                OrderUserResponse assignedDeliveryUser,
                OrderAddressResponse address, List<OrderItemResponse> items) {}
  public record OrderSummaryResponse(String orderNumber, OrderStatus status,
                                     String statusDescriptionKey,
                                     String currency, BigDecimal totalAmount,
                                     Integer totalItems, String city,
                                     boolean guestOrder, String guestLinkSlug,
                                     Instant createdAt) {}
  public record OrderStatusHistoryResponse(OrderStatus previousStatus,
                                           OrderStatus newStatus,
                                           Long changedByUserId,
                                           String changedByRole, String note,
                                           DeliveryFailureReason failureReason,
                                           Instant changedAt) {}
}
