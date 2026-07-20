package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.order.OrderDtos.*;
import com.smart.ecommerce.entity.*;
import java.util.Comparator;
import org.springframework.stereotype.Component;

@Component public class OrderMapper {
 public OrderResponse toResponse(Order o){ return new OrderResponse(o.getId(),o.getOrderNumber(),o.getStatus(),o.getCurrency(),o.getSubtotal(),o.getDeliveryFee(),o.getDiscountAmount(),o.getTotalAmount(),o.getTotalItems(),o.getCustomerNotes(),o.getCancellationReason(),o.getCreatedAt(),o.getUpdatedAt(),o.getCancelledAt(),o.getCompletedAt(),address(o),o.getItems().stream().sorted(Comparator.comparing(OrderItem::getId, Comparator.nullsLast(Long::compareTo))).map(this::toItem).toList()); }
 public OrderSummaryResponse toSummary(Order o){ return new OrderSummaryResponse(o.getId(),o.getOrderNumber(),o.getStatus(),o.getCurrency(),o.getTotalAmount(),o.getTotalItems(),o.getCity(),o.getCreatedAt()); }
 public OrderItemResponse toItem(OrderItem i){ return new OrderItemResponse(i.getId(),i.getProductId(),i.getProductName(),i.getProductImageUrl(),i.getQuantity(),i.getUnitPrice(),i.getCurrency(),i.getLineTotal()); }
 public OrderStatusHistoryResponse toHistory(OrderStatusHistory h){ return new OrderStatusHistoryResponse(h.getPreviousStatus(),h.getNewStatus(),h.getChangedByUserId(),h.getChangedByRole(),h.getNote(),h.getChangedAt()); }
 private OrderAddressResponse address(Order o){ return new OrderAddressResponse(o.getRecipientName(),o.getPhoneNumber(),o.getCity(),o.getLatitude(),o.getLongitude(),o.getArea(),o.getStreet(),o.getAdditionalDirections()); }
}
