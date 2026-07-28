package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.order.GuestSalesDtos.*;
import com.smart.ecommerce.service.EmployeeSalesLinkService;
import com.smart.ecommerce.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employee/sales-links")
public class EmployeeSalesLinkController {
  private final EmployeeSalesLinkService service;
  private final MessageUtil messages;

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','SUB_ADMIN')")
  public ApiResponse<SalesLinkResponse> create(
      @Valid @RequestBody CreateSalesLinkRequest request) {
    return ApiResponse.success(messages.getMessage("sales_link.created"),
                               service.create(request));
  }
}
