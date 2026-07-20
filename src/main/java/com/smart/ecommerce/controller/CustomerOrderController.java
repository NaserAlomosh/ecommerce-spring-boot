package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.*;import com.smart.ecommerce.dto.order.OrderDtos.*;import com.smart.ecommerce.enums.OrderStatus;import com.smart.ecommerce.service.OrderService;import jakarta.validation.Valid;import java.util.List;import lombok.RequiredArgsConstructor;import org.springframework.data.domain.Pageable;import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor @RequestMapping("/api/v1/customer/orders")
public class CustomerOrderController { private final OrderService service;
 @PostMapping public ApiResponse<OrderResponse> create(@Valid @RequestBody CreateOrderRequest r){ return ApiResponse.success("Order created", service.create(r)); }
 @GetMapping public ApiResponse<PaginationResponse<OrderSummaryResponse>> list(@RequestParam(required=false) OrderStatus status, Pageable pageable){ return ApiResponse.success("Orders", service.customerList(status,pageable)); }
 @GetMapping("/{orderId}") public ApiResponse<OrderResponse> get(@PathVariable Long orderId){ return ApiResponse.success("Order", service.customerGet(orderId)); }
 @GetMapping("/number/{orderNumber}") public ApiResponse<OrderResponse> getNumber(@PathVariable String orderNumber){ return ApiResponse.success("Order", service.customerGetNumber(orderNumber)); }
 @PatchMapping("/{orderId}/cancel") public ApiResponse<OrderResponse> cancel(@PathVariable Long orderId,@Valid @RequestBody CancelOrderRequest r){ return ApiResponse.success("Order cancelled", service.customerCancel(orderId,r)); }
 @GetMapping("/{orderId}/history") public ApiResponse<List<OrderStatusHistoryResponse>> history(@PathVariable Long orderId){ return ApiResponse.success("Order history", service.customerHistory(orderId)); }
}
