package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.*;
import com.smart.ecommerce.dto.order.OrderDtos.*;
import com.smart.ecommerce.enums.OrderStatus;
import com.smart.ecommerce.service.OrderService;
import com.smart.ecommerce.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery/orders")
public class DeliveryOrderController {
  private final OrderService service;
  private final MessageUtil messages;
  @GetMapping
  public ApiResponse<PaginationResponse<OrderSummaryResponse>>
  list(@RequestParam(required = false) OrderStatus status, Pageable pageable) {
    return ApiResponse.success(messages.getMessage("order.list"),
                               service.deliveryList(status, pageable));
  }
  @GetMapping("/{orderNumber}")
  public ApiResponse<OrderResponse> get(@PathVariable String orderNumber) {
    return ApiResponse.success(messages.getMessage("order.loaded"),
                               service.deliveryGetNumber(orderNumber));
  }
  @PatchMapping("/{orderNumber}/status")
  public ApiResponse<OrderResponse>
  status(@PathVariable String orderNumber,
         @Valid @RequestBody UpdateOrderStatusRequest r) {
    return ApiResponse.success(messages.getMessage("order.status_updated"),
                               service.deliveryStatus(orderNumber, r));
  }
}
