package com.smart.ecommerce.dashboard.controller;

import com.smart.ecommerce.dashboard.dto.DashboardResponse;
import com.smart.ecommerce.dashboard.service.DashboardService;
import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.util.MessageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/dashboard")
@Tag(name = "Admin Dashboard")
public class DashboardController {
  private final DashboardService service;
  private final MessageUtil messages;

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Get dashboard overview",
             security = @SecurityRequirement(name = "bearerAuth"))
  public ApiResponse<DashboardResponse>
  overview() {
    return ApiResponse.success(messages.getMessage("dashboard.loaded"),
                               service.overview());
  }
}
