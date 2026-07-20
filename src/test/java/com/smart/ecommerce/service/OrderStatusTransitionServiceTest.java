package com.smart.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;

import com.smart.ecommerce.enums.OrderStatus;
import org.junit.jupiter.api.Test;

class OrderStatusTransitionServiceTest {
 private final OrderStatusTransitionService service = new OrderStatusTransitionService();
 @Test void allowsRequiredTransitions() {
  assertDoesNotThrow(() -> service.validate(OrderStatus.PENDING, OrderStatus.PROCESSING));
  assertDoesNotThrow(() -> service.validate(OrderStatus.PROCESSING, OrderStatus.OUT_FOR_DELIVERY));
  assertDoesNotThrow(() -> service.validate(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.COMPLETED));
  assertDoesNotThrow(() -> service.validate(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.FAILED));
  assertDoesNotThrow(() -> service.validate(OrderStatus.FAILED, OrderStatus.PROCESSING));
 }
 @Test void rejectsInvalidTransitionsAndFinalStates() {
  assertThrows(IllegalArgumentException.class, () -> service.validate(OrderStatus.PENDING, OrderStatus.COMPLETED));
  assertThrows(IllegalArgumentException.class, () -> service.validate(OrderStatus.COMPLETED, OrderStatus.PROCESSING));
  assertThrows(IllegalArgumentException.class, () -> service.validate(OrderStatus.CANCELLED, OrderStatus.PROCESSING));
 }
}
