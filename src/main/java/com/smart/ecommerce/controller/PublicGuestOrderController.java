package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.*;
import com.smart.ecommerce.dto.guest.GuestOrderDtos.*;
import com.smart.ecommerce.dto.order.OrderDtos.OrderResponse;
import com.smart.ecommerce.dto.product.ProductDtos.ProductResponse;
import com.smart.ecommerce.service.*;
import com.smart.ecommerce.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/guest-links")
public class PublicGuestOrderController {
  private final GuestOrderLinkService links;
  private final ProductService products;
  private final OrderService orders;
  private final MessageUtil messages;

  @GetMapping("/{slug}")
  public ApiResponse<GuestLinkResponse> link(@PathVariable String slug) {
    return ApiResponse.success(messages.getMessage("guest_link.loaded"),
                               links.getActive(slug));
  }
  @GetMapping("/{slug}/products")
  public ApiResponse<PaginationResponse<ProductResponse>> products(
      @PathVariable String slug, Pageable pageable) {
    links.requireActive(slug);
    return ApiResponse.success(messages.getMessage("products"),
                               products.listAvailable(pageable));
  }
  @PostMapping("/{slug}/orders")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<OrderResponse> create(
      @PathVariable String slug, @Valid @RequestBody GuestOrderRequest request) {
    return ApiResponse.success(messages.getMessage("order.created"),
                               orders.createGuest(slug, request));
  }
}
