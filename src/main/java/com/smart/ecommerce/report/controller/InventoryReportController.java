package com.smart.ecommerce.report.controller;
import com.smart.ecommerce.dto.*;
import com.smart.ecommerce.enums.InventoryMovementType;
import com.smart.ecommerce.report.dto.InventoryFilters.*;
import com.smart.ecommerce.report.dto.InventoryReportDtos.*;
import com.smart.ecommerce.report.service.InventoryReportService;
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
@RequestMapping("/api/v1/admin/reports/inventory")
@Tag(name = "Admin Inventory Reports")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class InventoryReportController {
  private final InventoryReportService service;
  private final MessageUtil messages;
  @GetMapping("/current")
  @Operation(summary = "Current inventory report. Unit cost is null unless " +
                       "modeled on Product.")
  public ApiResponse<PaginationResponse<CurrentInventoryRow>>
  current(@RequestParam(required = false) Long productId,
          @RequestParam(required = false) String productName,
          @RequestParam(required = false) Long categoryId,
          @RequestParam(required = false) Boolean active,
          @RequestParam(required = false) StockStatus stockStatus,
          @RequestParam(required = false) Integer minStock,
          @RequestParam(required = false) Integer maxStock,
          @RequestParam(required = false) BigDecimal minUnitCost,
          @RequestParam(required = false) BigDecimal maxUnitCost, Pageable p) {
    return ok(service.current(new Current(productId, productName, categoryId,
                                          active, stockStatus, minStock,
                                          maxStock, minUnitCost, maxUnitCost),
                              p));
  }
  @GetMapping("/low-stock")
  public ApiResponse<PaginationResponse<LowStockRow>>
  low(@RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Integer threshold,
      @RequestParam(required = false) Boolean active,
      @RequestParam(defaultValue = "TODAY") ReportPeriod period,
      @RequestParam(required = false) LocalDate dateFrom,
      @RequestParam(required = false) LocalDate dateTo, Pageable p) {
    return ok(service.lowStock(categoryId, threshold, active,
                               new Period(period, dateFrom, dateTo), p));
  }
  @GetMapping("/out-of-stock")
  public ApiResponse<PaginationResponse<OutOfStockRow>>
  out(@RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Boolean active, Pageable p) {
    return ok(service.outOfStock(categoryId, active, p));
  }
  @GetMapping("/valuation")
  public ApiResponse<ValuationResponse>
  valuation(@RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) StockStatus stockStatus,
            Pageable p) {
    return ok(service.valuation(categoryId, active, stockStatus, p));
  }
  @GetMapping("/movements")
  public ApiResponse<PaginationResponse<MovementRow>>
  movements(@RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) InventoryMovementType movementType,
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long performedByUserId,
            @RequestParam(defaultValue = "TODAY") ReportPeriod period,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(required = false) Integer minQuantityChange,
            @RequestParam(required = false) Integer maxQuantityChange,
            @RequestParam(required = false) MovementDirection direction,
            Pageable p) {
    return ok(service.movements(
        new Movement(productId, categoryId, movementType, orderNumber,
                     customerId, performedByUserId, period, dateFrom, dateTo,
                     minQuantityChange, maxQuantityChange, direction),
        p));
  }
  @GetMapping("/movements/summary")
  public ApiResponse<MovementSummaryResponse>
  summary(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
          @RequestParam(required = false) LocalDate dateFrom,
          @RequestParam(required = false) LocalDate dateTo,
          @RequestParam(required = false) Long productId,
          @RequestParam(required = false) Long categoryId) {
    return ok(service.movementSummary(new Period(period, dateFrom, dateTo),
                                      productId, categoryId));
  }
  @GetMapping("/products/{productId}/ledger")
  public ApiResponse<ProductInventoryLedgerResponse>
  ledger(@PathVariable Long productId,
         @RequestParam(defaultValue = "TODAY") ReportPeriod period,
         @RequestParam(required = false) LocalDate dateFrom,
         @RequestParam(required = false) LocalDate dateTo, Pageable p) {
    return ok(
        service.ledger(productId, new Period(period, dateFrom, dateTo), p));
  }
  @GetMapping("/never-sold")
  public ApiResponse<PaginationResponse<NeverSoldRow>>
  never(@RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) Boolean active,
        @RequestParam(required = false) LocalDate createdBefore, Pageable p) {
    return ok(service.neverSold(categoryId, active, createdBefore, p));
  }
  @GetMapping("/most-adjusted")
  public ApiResponse<List<MostAdjustedRow>>
  adjusted(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
           @RequestParam(required = false) LocalDate dateFrom,
           @RequestParam(required = false) LocalDate dateTo,
           @RequestParam(defaultValue = "10") int limit,
           @RequestParam(defaultValue = "ADJUSTMENT_COUNT") String sortBy) {
    return ok(service.mostAdjusted(new Period(period, dateFrom, dateTo), limit,
                                   sortBy));
  }
  @GetMapping("/restorations")
  public ApiResponse<RestorationResponse>
  restorations(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
               @RequestParam(required = false) LocalDate dateFrom,
               @RequestParam(required = false) LocalDate dateTo,
               @RequestParam(required = false)
               InventoryMovementType movementType, Pageable p) {
    return ok(service.restorations(new Period(period, dateFrom, dateTo),
                                   movementType, p));
  }
  @GetMapping("/reconciliation")
  public ApiResponse<ReconciliationResponse>
  reconciliation(@RequestParam(defaultValue = "false") boolean onlyMismatched) {
    return ok(service.reconciliation(onlyMismatched));
  }
  @GetMapping("/turnover")
  public ApiResponse<TurnoverResponse>
  turnover(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
           @RequestParam(required = false) LocalDate dateFrom,
           @RequestParam(required = false) LocalDate dateTo,
           @RequestParam(required = false) Long productId,
           @RequestParam(required = false) Long categoryId) {
    return ok(service.turnover(new Period(period, dateFrom, dateTo), productId,
                               categoryId));
  }
  @GetMapping("/movements/trend")
  public ApiResponse<InventoryTrendResponse>
  trend(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
        @RequestParam(required = false) LocalDate dateFrom,
        @RequestParam(required = false) LocalDate dateTo,
        @RequestParam(defaultValue = "DAY") ReportGranularity granularity,
        @RequestParam(required = false) Long productId,
        @RequestParam(required = false) Long categoryId) {
    return ok(service.trend(new Period(period, dateFrom, dateTo), granularity,
                            productId, categoryId));
  }
  private <T> ApiResponse<T> ok(T d) {
    return ApiResponse.success(messages.getMessage("report.loaded"), d);
  }
}
