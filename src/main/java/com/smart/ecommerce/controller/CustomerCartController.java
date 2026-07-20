package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;import com.smart.ecommerce.dto.cart.CartDtos.*;import com.smart.ecommerce.service.CartService;import jakarta.validation.Valid;import lombok.RequiredArgsConstructor;import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor @RequestMapping("/api/v1/customer/cart")
public class CustomerCartController { private final CartService service;
 @GetMapping public ApiResponse<CartResponse> get(){ return ApiResponse.success("Cart", service.get()); }
 @PostMapping("/items") public ApiResponse<CartResponse> add(@Valid @RequestBody AddCartItemRequest r){ return ApiResponse.success("Cart item added", service.add(r)); }
 @PatchMapping("/items/{cartItemId}") public ApiResponse<CartResponse> update(@PathVariable Long cartItemId,@Valid @RequestBody UpdateCartItemQuantityRequest r){ return ApiResponse.success("Cart item updated", service.update(cartItemId,r)); }
 @DeleteMapping("/items/{cartItemId}") public ApiResponse<Void> remove(@PathVariable Long cartItemId){ service.remove(cartItemId); return ApiResponse.success("Cart item removed", null); }
 @DeleteMapping("/items") public ApiResponse<Void> clear(){ service.clear(); return ApiResponse.success("Cart cleared", null); }
}
