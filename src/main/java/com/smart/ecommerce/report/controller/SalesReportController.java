package com.smart.ecommerce.report.controller;
import com.smart.ecommerce.dto.*;
import com.smart.ecommerce.report.dto.*;
import com.smart.ecommerce.report.dto.ReportDtos.*;
import com.smart.ecommerce.report.service.SalesReportService;
import com.smart.ecommerce.report.util.*;
import com.smart.ecommerce.util.MessageUtil;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/reports")
@Tag(name = "Admin Sales Reports")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class SalesReportController {
  private final SalesReportService service;
  private final MessageUtil messages;
  @GetMapping("/sales/summary")
  @Operation(
      summary =
          "Sales summary; realized revenue uses COMPLETED orders only and " +
          "completion date boundaries are business-timezone inclusive dates")
  public ApiResponse<SalesSummaryResponse>
  summary(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
          @RequestParam(required = false) LocalDate dateFrom,
          @RequestParam(required = false) LocalDate dateTo) {
    return ok(service.summary(period, dateFrom, dateTo));
  }
  @GetMapping("/sales/comparison")
  public ApiResponse<SalesComparisonResponse>
  comparison(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
             @RequestParam(required = false) LocalDate dateFrom,
             @RequestParam(required = false) LocalDate dateTo) {
    return ok(service.comparison(period, dateFrom, dateTo));
  }
  @GetMapping("/sales/trend")
  public ApiResponse<SalesTrendResponse>
  trend(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
        @RequestParam(required = false) LocalDate dateFrom,
        @RequestParam(required = false) LocalDate dateTo,
        @RequestParam(defaultValue = "DAY") ReportGranularity granularity) {
    return ok(service.trend(period, dateFrom, dateTo, granularity));
  }
  @GetMapping("/orders/status-summary")
  public ApiResponse<OrderStatusSummaryResponse>
  status(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
         @RequestParam(required = false) LocalDate dateFrom,
         @RequestParam(required = false) LocalDate dateTo) {
    return ok(service.statusSummary(period, dateFrom, dateTo));
  }
  @GetMapping("/sales/orders")
  public ApiResponse<PaginationResponse<SalesOrderRow>>
  orders(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
         @RequestParam(required = false) LocalDate dateFrom,
         @RequestParam(required = false) LocalDate dateTo,
         @RequestParam(required = false) String orderNumber,
         @RequestParam(required = false) Long customerId,
         @RequestParam(required = false) String customerName,
         @RequestParam(required = false) BigDecimal minAmount,
         @RequestParam(required = false) BigDecimal maxAmount,
         @RequestParam(required = false) Long productId,
         @RequestParam(required = false) Long categoryId,
         @RequestParam(required = false) Long assignedDeliveryUserId,
         @RequestParam(required = false) String currency, Pageable pageable) {
    return ok(service.orders(
        new SalesOrderFilter(period, dateFrom, dateTo, orderNumber, customerId,
                             customerName, minAmount, maxAmount, productId,
                             categoryId, assignedDeliveryUserId, currency),
        pageable));
  }
  @GetMapping("/sales/top-products")
  public ApiResponse<List<TopProductResponse>>
  top(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
      @RequestParam(required = false) LocalDate dateFrom,
      @RequestParam(required = false) LocalDate dateTo,
      @RequestParam(defaultValue = "10") Integer limit,
      @RequestParam(defaultValue = "QUANTITY_SOLD") String sortBy) {
    return ok(service.topProducts(period, dateFrom, dateTo, limit, sortBy));
  }
  @GetMapping("/sales/categories")
  public ApiResponse<PaginationResponse<CategorySalesResponse>>
  categories(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
             @RequestParam(required = false) LocalDate dateFrom,
             @RequestParam(required = false) LocalDate dateTo,
             Pageable pageable) {
    return ok(service.categories(period, dateFrom, dateTo, pageable));
  }
  @GetMapping("/sales/customers")
  public ApiResponse<PaginationResponse<CustomerSalesResponse>>
  customers(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String customerName,
            Pageable pageable) {
    return ok(
        service.customers(new CustomerSalesFilter(period, dateFrom, dateTo,
                                                  customerId, customerName),
                          pageable));
  }
  @GetMapping("/sales/products/{productId}")
  public ApiResponse<ProductSalesResponse>
  product(@PathVariable Long productId,
          @RequestParam(defaultValue = "TODAY") ReportPeriod period,
          @RequestParam(required = false) LocalDate dateFrom,
          @RequestParam(required = false) LocalDate dateTo,
          @RequestParam(defaultValue = "DAY") ReportGranularity granularity) {
    return ok(
        service.product(productId, period, dateFrom, dateTo, granularity));
  }
  private <T> ApiResponse<T> ok(T data) {
    return ApiResponse.success(messages.getMessage("report.loaded"), data);
  }
}
