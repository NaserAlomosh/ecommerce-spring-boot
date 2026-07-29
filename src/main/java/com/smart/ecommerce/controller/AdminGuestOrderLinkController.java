package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.guest.GuestOrderDtos.*;
import com.smart.ecommerce.service.GuestOrderLinkService;
import com.smart.ecommerce.util.MessageUtil;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/guest-links")
public class AdminGuestOrderLinkController {
  private final GuestOrderLinkService service;
  private final MessageUtil messages;

  @GetMapping
  public ApiResponse<List<GuestLinkResponse>> list() {
    return ApiResponse.success(messages.getMessage("guest_link.loaded"),
                               service.list());
  }
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<GuestLinkResponse> create(
      @Valid @RequestBody GuestLinkRequest request) {
    return ApiResponse.success(messages.getMessage("guest_link.created"),
                               service.create(request));
  }
  @PutMapping("/{id}")
  public ApiResponse<GuestLinkResponse> update(
      @PathVariable Long id, @Valid @RequestBody GuestLinkRequest request) {
    return ApiResponse.success(messages.getMessage("guest_link.updated"),
                               service.update(id, request));
  }
  @PatchMapping("/{id}/status")
  public ApiResponse<GuestLinkResponse> status(
      @PathVariable Long id, @Valid @RequestBody GuestLinkStatusRequest request) {
    return ApiResponse.success(messages.getMessage("guest_link.status_updated"),
                               service.setActive(id, request));
  }
}
