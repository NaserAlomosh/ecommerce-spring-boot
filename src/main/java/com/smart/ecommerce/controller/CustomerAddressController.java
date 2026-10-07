package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.address.AddressDtos.*;
import com.smart.ecommerce.service.CustomerAddressService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer/addresses")
public class CustomerAddressController {
  private final CustomerAddressService service;
  @GetMapping
  public ApiResponse<List<AddressResponse>> list() {
    return ApiResponse.success("Addresses", service.list());
  }
  @GetMapping("/{id}")
  public ApiResponse<AddressResponse> get(@PathVariable Long id) {
    return ApiResponse.success("Address", service.get(id));
  }
  @PostMapping
  public ApiResponse<AddressResponse>
  create(@Valid @RequestBody CreateAddressRequest r) {
    return ApiResponse.success("Address created", service.create(r));
  }
  @PutMapping("/{id}")
  public ApiResponse<AddressResponse>
  update(@PathVariable Long id, @Valid @RequestBody UpdateAddressRequest r) {
    return ApiResponse.success("Address updated", service.update(id, r));
  }
  @PatchMapping("/{id}/default")
  public ApiResponse<AddressResponse> setDefault(@PathVariable Long id) {
    return ApiResponse.success("Default address updated",
                               service.setDefault(id));
  }
  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ApiResponse.success("Address deleted", null);
  }
}
