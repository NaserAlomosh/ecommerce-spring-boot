package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.*;
import com.smart.ecommerce.dto.order.OrderDtos.*;
import com.smart.ecommerce.enums.OrderStatus;
import com.smart.ecommerce.service.OrderService;
import com.smart.ecommerce.util.MessageUtil;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {
  private final OrderService service;
  private final MessageUtil messages;
  @GetMapping
  public ApiResponse<PaginationResponse<OrderSummaryResponse>>
  list(@RequestParam(required = false) OrderStatus status,
       @RequestParam(required = false) String orderNumber,
       @RequestParam(required = false) String customer,
       @RequestParam(required = false) Instant fromDate,
       @RequestParam(required = false) Instant toDate, Pageable pageable) {
    return ApiResponse.success(messages.getMessage("order.list"),
                               service.adminList(status, orderNumber, customer,
                                                 fromDate, toDate, pageable));
  }
  @GetMapping("/{orderNumber}")
  public ApiResponse<OrderResponse>
  getNumber(@PathVariable String orderNumber) {
    return ApiResponse.success(messages.getMessage("order.loaded"),
                               service.adminGetNumber(orderNumber));
  }
  @PatchMapping("/{orderNumber}/delivery")
  public ApiResponse<OrderResponse>
  assign(@PathVariable String orderNumber,
         @Valid @RequestBody AssignDeliveryRequest r) {
    return ApiResponse.success(messages.getMessage("order.delivery_assigned"),
                               service.assignDelivery(orderNumber, r));
  }
  @PatchMapping("/{orderNumber}/status")
  public ApiResponse<OrderResponse>
  status(@PathVariable String orderNumber,
         @Valid @RequestBody UpdateOrderStatusRequest r) {
    return ApiResponse.success(messages.getMessage("order.status_updated"),
                               service.adminStatus(orderNumber, r));
  }
  @PatchMapping("/{orderNumber}/cancel")
  public ApiResponse<OrderResponse>
  cancel(@PathVariable String orderNumber,
         @Valid @RequestBody CancelOrderRequest r) {
    return ApiResponse.success(messages.getMessage("order.cancelled"),
                               service.adminCancel(orderNumber, r));
  }
  @GetMapping("/{orderNumber}/history")
  public ApiResponse<List<OrderStatusHistoryResponse>>
  history(@PathVariable String orderNumber) {
    return ApiResponse.success(messages.getMessage("order.history"),
                               service.adminHistory(orderNumber));
  }
}
