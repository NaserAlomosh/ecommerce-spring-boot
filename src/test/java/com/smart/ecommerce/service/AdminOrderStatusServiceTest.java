package com.smart.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smart.ecommerce.dto.order.OrderDtos.UpdateOrderStatusRequest;
import com.smart.ecommerce.entity.Order;
import com.smart.ecommerce.entity.OrderItem;
import com.smart.ecommerce.entity.Product;
import com.smart.ecommerce.entity.User;
import com.smart.ecommerce.enums.OrderStatus;
import com.smart.ecommerce.repository.OrderRepository;
import com.smart.ecommerce.repository.ProductRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminOrderStatusServiceTest {
  @Mock private CustomerContextService context;
  @Mock private InventoryService inventory;
  @Mock private ProductRepository products;
  @Mock private OrderRepository orders;
  @Mock private OrderMapper mapper;
  @InjectMocks private OrderService service;

  @Test
  void adminCanMoveDirectlyFromCompletedToFailedWithOptionalDetails() {
    User admin = user(9L);
    Order order = order(OrderStatus.COMPLETED);
    order.setCompletedAt(Instant.now());
    when(context.currentCustomer()).thenReturn(admin);
    when(orders.lockWithItemsByOrderNumber("ORD-1"))
        .thenReturn(Optional.of(order));

    service.adminStatus(
        "ORD-1", new UpdateOrderStatusRequest(OrderStatus.FAILED, null, null, null));

    assertEquals(OrderStatus.FAILED, order.getStatus());
    assertNull(order.getCompletedAt());
    assertEquals(1, order.getStatusHistory().size());
    assertNull(order.getStatusHistory().getFirst().getNote());
  }

  @Test
  void adminCanReopenCancelledOrderAndInventoryIsReservedAgain() {
    User admin = user(9L);
    Order order = order(OrderStatus.CANCELLED);
    order.setCancelledAt(Instant.now());
    Product product = new Product();
    product.setId(20L);
    product.setStockQuantity(10);
    OrderItem item = new OrderItem();
    item.setProductId(20L);
    item.setQuantity(3);
    order.getItems().add(item);
    when(context.currentCustomer()).thenReturn(admin);
    when(orders.lockWithItemsByOrderNumber("ORD-1"))
        .thenReturn(Optional.of(order));
    when(products.lockWithImagesByIdIn(List.of(20L))).thenReturn(List.of(product));

    service.adminStatus(
        "ORD-1",
        new UpdateOrderStatusRequest(
            OrderStatus.PROCESSING, "Cancellation reversed", null, null));

    assertEquals(OrderStatus.PROCESSING, order.getStatus());
    assertNull(order.getCancelledAt());
    verify(inventory)
        .recordOrderStatusAdjustment(
            product, -3, order, admin, "Cancellation reversed");
  }

  private Order order(OrderStatus status) {
    Order order = new Order();
    order.setId(1L);
    order.setOrderNumber("ORD-1");
    order.setStatus(status);
    return order;
  }

  private User user(Long id) {
    User user = new User();
    user.setId(id);
    return user;
  }
}
