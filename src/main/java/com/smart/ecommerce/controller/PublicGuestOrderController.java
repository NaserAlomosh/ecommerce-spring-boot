package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.guest.GuestOrderDtos.GuestOrderRequest;
import com.smart.ecommerce.dto.order.OrderDtos.OrderResponse;
import com.smart.ecommerce.dto.website.WebsiteDtos.GuestOrderLinkResponse;
import com.smart.ecommerce.service.OrderService;
import com.smart.ecommerce.service.WebsiteService;
import com.smart.ecommerce.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/guest-order-link")
public class PublicGuestOrderController {
  private final WebsiteService websiteService;
  private final OrderService orderService;
  private final MessageUtil messages;

  @GetMapping
  public ApiResponse<GuestOrderLinkResponse> link() {
    return ApiResponse.success(messages.getMessage("guest_order_link.loaded"),
                               websiteService.guestOrderLink());
  }

  @PostMapping("/orders")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<OrderResponse> createOrder(
      @Valid @RequestBody GuestOrderRequest request) {
    return ApiResponse.success(messages.getMessage("order.created"),
                               orderService.createGuest(request));
  }
}
