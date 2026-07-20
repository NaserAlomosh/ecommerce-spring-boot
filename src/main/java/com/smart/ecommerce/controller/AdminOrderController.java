package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.*;import com.smart.ecommerce.dto.order.OrderDtos.*;import com.smart.ecommerce.enums.OrderStatus;import com.smart.ecommerce.service.OrderService;import jakarta.validation.Valid;import java.time.Instant;import java.util.List;import lombok.RequiredArgsConstructor;import org.springframework.data.domain.Pageable;import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor @RequestMapping("/api/v1/admin/orders")
public class AdminOrderController { private final OrderService service;
 @GetMapping public ApiResponse<PaginationResponse<OrderSummaryResponse>> list(@RequestParam(required=false) OrderStatus status,@RequestParam(required=false) String orderNumber,@RequestParam(required=false) String customer,@RequestParam(required=false) Instant fromDate,@RequestParam(required=false) Instant toDate, Pageable pageable){ return ApiResponse.success("Orders", service.adminList(status,orderNumber,customer,fromDate,toDate,pageable)); }
 @GetMapping("/{orderId}") public ApiResponse<OrderResponse> get(@PathVariable Long orderId){ return ApiResponse.success("Order", service.adminGet(orderId)); }
 @GetMapping("/number/{orderNumber}") public ApiResponse<OrderResponse> getNumber(@PathVariable String orderNumber){ return ApiResponse.success("Order", service.adminGetNumber(orderNumber)); }
 @PatchMapping("/{orderId}/status") public ApiResponse<OrderResponse> status(@PathVariable Long orderId,@Valid @RequestBody UpdateOrderStatusRequest r){ return ApiResponse.success("Order status updated", service.adminStatus(orderId,r)); }
 @PatchMapping("/{orderId}/cancel") public ApiResponse<OrderResponse> cancel(@PathVariable Long orderId,@Valid @RequestBody CancelOrderRequest r){ return ApiResponse.success("Order cancelled", service.adminCancel(orderId,r)); }
 @GetMapping("/{orderId}/history") public ApiResponse<List<OrderStatusHistoryResponse>> history(@PathVariable Long orderId){ return ApiResponse.success("Order history", service.adminHistory(orderId)); }
}
