package com.smart.ecommerce.service;

import com.smart.ecommerce.enums.OrderStatus;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusTransitionService {
 private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
  OrderStatus.PENDING, Set.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED),
  OrderStatus.PROCESSING, Set.of(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.CANCELLED),
  OrderStatus.OUT_FOR_DELIVERY, Set.of(OrderStatus.COMPLETED, OrderStatus.FAILED),
  OrderStatus.FAILED, Set.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED),
  OrderStatus.COMPLETED, Set.of(),
  OrderStatus.CANCELLED, Set.of()
 );
 public void validate(OrderStatus from, OrderStatus to){ if(!ALLOWED.getOrDefault(from, Set.of()).contains(to)) throw new IllegalArgumentException("order.error.invalid_transition"); }
 public boolean canAdminCancel(OrderStatus status){ return status==OrderStatus.PENDING || status==OrderStatus.PROCESSING || status==OrderStatus.FAILED; }
 public boolean canCustomerCancel(OrderStatus status){ return status==OrderStatus.PENDING; }
}
