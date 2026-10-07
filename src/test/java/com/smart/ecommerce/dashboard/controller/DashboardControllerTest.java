package com.smart.ecommerce.dashboard.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.smart.ecommerce.dashboard.dto.DashboardResponse;
import com.smart.ecommerce.dashboard.dto.DashboardResponse.*;
import com.smart.ecommerce.dashboard.service.DashboardService;
import com.smart.ecommerce.util.MessageUtil;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DashboardController.class)
@Import(DashboardControllerTest.MethodSecurityTestConfig.class)
class DashboardControllerTest {
  @Autowired MockMvc mvc;
  @MockBean DashboardService service;
  @MockBean MessageUtil messages;

  @Test
  void adminCanLoadDashboard() throws Exception {
    when(messages.getMessage("dashboard.loaded"))
        .thenReturn("Dashboard overview loaded successfully");
    when(service.overview()).thenReturn(emptyResponse());
    mvc.perform(
           get("/api/v1/admin/dashboard").with(user("admin").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.orders.totalOrders").value(0));
  }

  @Test
  void subAdminIsForbiddenByMethodSecurity() throws Exception {
    mvc.perform(
           get("/api/v1/admin/dashboard").with(user("sub").roles("SUB_ADMIN")))
        .andExpect(status().isForbidden());
  }

  private DashboardResponse emptyResponse() {
    return new DashboardResponse(
        new SalesSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
        new OrderSummary(0, 0, 0, 0, 0, 0, 0), new ProductSummary(0, 0, 0),
        new CategorySummary(0), new CustomerSummary(0),
        new InventorySummary(0, 0), new ReviewSummary(0, BigDecimal.ZERO),
        List.of(), List.of(), List.of(), List.of());
  }

  @EnableMethodSecurity
  static class MethodSecurityTestConfig {}
}
