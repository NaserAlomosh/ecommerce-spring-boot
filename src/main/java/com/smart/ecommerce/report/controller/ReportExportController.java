package com.smart.ecommerce.report.controller;
import com.smart.ecommerce.report.dto.*;
import com.smart.ecommerce.report.dto.ReportDtos.ExportedReport;
import com.smart.ecommerce.report.service.ReportExportService;
import com.smart.ecommerce.report.util.ReportPeriod;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/reports/sales")
@Tag(name = "Admin Report PDF Export")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class ReportExportController {
  private final ReportExportService service;
  @GetMapping(value = "/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
  @Operation(summary = "Export sales summary and order details as PDF",
             responses = @ApiResponse(
                 responseCode = "200",
                 content = @Content(mediaType = "application/pdf")))
  public ResponseEntity<ByteArrayResource>
  sales(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
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
        @RequestParam(required = false) String currency) {
    return pdf(service.salesPdf(
        new SalesOrderFilter(period, dateFrom, dateTo, orderNumber, customerId,
                             customerName, minAmount, maxAmount, productId,
                             categoryId, assignedDeliveryUserId, currency),
        loc()));
  }
  @GetMapping(value = "/top-products/export/pdf",
              produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<ByteArrayResource>
  top(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
      @RequestParam(required = false) LocalDate dateFrom,
      @RequestParam(required = false) LocalDate dateTo,
      @RequestParam(defaultValue = "100") Integer limit,
      @RequestParam(defaultValue = "QUANTITY_SOLD") String sortBy) {
    return pdf(
        service.topProductsPdf(period, dateFrom, dateTo, limit, sortBy, loc()));
  }
  @GetMapping(value = "/categories/export/pdf",
              produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<ByteArrayResource>
  categories(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
             @RequestParam(required = false) LocalDate dateFrom,
             @RequestParam(required = false) LocalDate dateTo,
             Pageable pageable) {
    return pdf(
        service.categoriesPdf(period, dateFrom, dateTo, pageable, loc()));
  }
  @GetMapping(value = "/customers/export/pdf",
              produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<ByteArrayResource>
  customers(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String customerName,
            Pageable pageable) {
    return pdf(
        service.customersPdf(new CustomerSalesFilter(period, dateFrom, dateTo,
                                                     customerId, customerName),
                             pageable, loc()));
  }
  private Locale loc() { return LocaleContextHolder.getLocale(); }
  private ResponseEntity<ByteArrayResource> pdf(ExportedReport r) {
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + r.filename() + "\"")
        .header(HttpHeaders.CACHE_CONTROL, "no-store")
        .contentLength(r.content().length)
        .body(new ByteArrayResource(r.content()));
  }
}
