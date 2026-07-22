package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.order.OrderDtos.*;
import com.smart.ecommerce.entity.*;
import java.util.Comparator;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
  public OrderResponse toResponse(Order o) {
    return new OrderResponse(
        o.getOrderNumber(), o.getStatus(), statusKey(o.getStatus()),
        o.getCurrency(), o.getSubtotal(), o.getTotalAmount(), o.getTotalItems(),
        o.getCustomerNote(), o.getFailureReason(), failureKey(o),
        o.getFailureNote(), o.getCreatedAt(), o.getUpdatedAt(),
        o.getCancelledAt(), o.getCompletedAt(), user(o.getCustomer()),
        user(o.getAssignedDeliveryUser()), address(o),
        o.getItems()
            .stream()
            .sorted(Comparator.comparing(OrderItem::getId,
                                         Comparator.nullsLast(Long::compareTo)))
            .map(this::toItem)
            .toList());
  }
  public OrderSummaryResponse toSummary(Order o) {
    return new OrderSummaryResponse(o.getOrderNumber(), o.getStatus(),
                                    statusKey(o.getStatus()), o.getCurrency(),
                                    o.getTotalAmount(), o.getTotalItems(),
                                    o.getCity(), o.getCreatedAt());
  }
  public OrderItemResponse toItem(OrderItem i) {
    return new OrderItemResponse(
        i.getProductId(), i.getProductName(), i.getProductImageUrl(),
        i.getQuantity(), i.getUnitPrice(), i.getCurrency(), i.getLineTotal());
  }
  public OrderStatusHistoryResponse toHistory(OrderStatusHistory h) {
    return new OrderStatusHistoryResponse(
        h.getPreviousStatus(), h.getNewStatus(), h.getChangedByUserId(),
        h.getChangedByRole(), h.getNote(), h.getFailureReason(),
        h.getChangedAt());
  }
  private OrderAddressResponse address(Order o) {
    return new OrderAddressResponse(o.getRecipientName(), o.getPhoneNumber(),
                                    o.getCity(), o.getLatitude(),
                                    o.getLongitude(), o.getArea(),
                                    o.getStreet(), o.getAdditionalDirections());
  }
  private OrderUserResponse user(User u) {
    return u == null ? null
                     : new OrderUserResponse(
                           (u.getFirstName() + " " + u.getLastName()).trim(),
                           u.getEmail(), u.getPhoneNumber());
  }
  private String statusKey(com.smart.ecommerce.enums.OrderStatus s) {
    return "order.status." + s.name().toLowerCase();
  }
  private String failureKey(Order o) {
    return o.getFailureReason() == null
        ? null
        : "order.failure_reason." + o.getFailureReason().name().toLowerCase();
  }
}
