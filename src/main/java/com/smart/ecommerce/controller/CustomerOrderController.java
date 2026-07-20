package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.*;import com.smart.ecommerce.dto.order.OrderDtos.*;import com.smart.ecommerce.enums.OrderStatus;import com.smart.ecommerce.service.OrderService;import com.smart.ecommerce.util.MessageUtil;import jakarta.validation.Valid;import lombok.RequiredArgsConstructor;import org.springframework.data.domain.Pageable;import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor @RequestMapping("/api/v1/customer/orders")
public class CustomerOrderController { private final OrderService service; private final MessageUtil messages;
 @PostMapping public ApiResponse<OrderResponse> create(@Valid @RequestBody CreateOrderRequest r){ return ApiResponse.success(messages.getMessage("order.created"), service.create(r)); }
 @GetMapping public ApiResponse<PaginationResponse<OrderSummaryResponse>> list(@RequestParam(required=false) OrderStatus status, Pageable pageable){ return ApiResponse.success(messages.getMessage("order.list"), service.customerList(status,pageable)); }
 @GetMapping("/{orderNumber}") public ApiResponse<OrderResponse> getNumber(@PathVariable String orderNumber){ return ApiResponse.success(messages.getMessage("order.loaded"), service.customerGetNumber(orderNumber)); }
}
