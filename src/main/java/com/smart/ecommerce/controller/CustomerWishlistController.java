package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.wishlist.WishlistDtos.*;
import com.smart.ecommerce.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer/wishlist")
public class CustomerWishlistController {
  private final WishlistService service;
  @GetMapping
  public ApiResponse<WishlistResponse> list() {
    return ApiResponse.success("Wishlist", service.list());
  }
  @PostMapping
  public ApiResponse<WishlistResponse>
  add(@Valid @RequestBody AddWishlistItemRequest r) {
    return ApiResponse.success("Wishlist item added", service.add(r));
  }
  @DeleteMapping("/{productId}")
  public ApiResponse<Void> remove(@PathVariable Long productId) {
    service.remove(productId);
    return ApiResponse.success("Wishlist item removed", null);
  }
  @GetMapping("/check/{productId}")
  public ApiResponse<WishlistCheckResponse>
  check(@PathVariable Long productId) {
    return ApiResponse.success("Wishlist check", service.check(productId));
  }
}
