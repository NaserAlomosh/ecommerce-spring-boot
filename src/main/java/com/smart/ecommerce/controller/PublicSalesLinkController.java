package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.*;
import com.smart.ecommerce.dto.order.GuestSalesDtos.GuestOrderRequest;
import com.smart.ecommerce.dto.order.OrderDtos.OrderResponse;
import com.smart.ecommerce.dto.product.ProductDtos.ProductResponse;
import com.smart.ecommerce.service.EmployeeSalesLinkService;
import com.smart.ecommerce.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/sales-links/{token}")
public class PublicSalesLinkController {
  private final EmployeeSalesLinkService service;
  private final MessageUtil messages;

  @GetMapping("/products")
  public ApiResponse<PaginationResponse<ProductResponse>> products(
      @PathVariable String token, @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Boolean featured, Pageable pageable) {
    return ApiResponse.success(messages.getMessage("products"),
                               service.products(token, categoryId, featured, pageable));
  }
  @GetMapping("/products/{productId}")
  public ApiResponse<ProductResponse> product(@PathVariable String token,
                                               @PathVariable Long productId) {
    return ApiResponse.success(messages.getMessage("product"),
                               service.product(token, productId));
  }
  @PostMapping("/orders")
  public ApiResponse<OrderResponse> order(@PathVariable String token,
                                          @Valid @RequestBody GuestOrderRequest request) {
    return ApiResponse.success(messages.getMessage("order.created"),
                               service.order(token, request));
  }
}
