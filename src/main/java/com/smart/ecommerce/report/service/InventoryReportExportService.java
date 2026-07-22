package com.smart.ecommerce.report.service;
import com.smart.ecommerce.report.dto.InventoryFilters.*;
import com.smart.ecommerce.report.dto.InventoryReportDtos.*;
import com.smart.ecommerce.report.dto.ReportDtos.ExportedReport;
import com.smart.ecommerce.report.generator.pdf.PdfInventoryReportGenerator;
import com.smart.ecommerce.report.util.*;
import java.time.LocalDate;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
@Service
public class InventoryReportExportService {
  private final InventoryReportService svc;
  private final ReportProperties props;
  private final PdfInventoryReportGenerator pdf;
  private final DateRangeResolver ranges;
  public InventoryReportExportService(InventoryReportService svc,
                                      ReportProperties props,
                                      PdfInventoryReportGenerator pdf,
                                      DateRangeResolver ranges) {
    this.svc = svc;
    this.props = props;
    this.pdf = pdf;
    this.ranges = ranges;
  }
  private void check(long n) {
    if (n > props.maxPdfRows())
      throw new IllegalArgumentException("report.error.pdf_row_limit_exceeded");
  }
  public ExportedReport current(Current f, Locale l) {
    var r = svc.current(f, PageRequest.of(0, props.maxPdfRows() + 1));
    check(r.totalElements());
    return er("inventory-current", pdf.current(r.content(), l, user(), "UTC"),
              r.content().size());
  }
  public ExportedReport low(Long c, Integer t, Boolean a, Period p, Locale l) {
    range(p);
    var r = svc.lowStock(c, t, a, p, PageRequest.of(0, props.maxPdfRows() + 1));
    check(r.totalElements());
    return er("inventory-low-stock", pdf.low(r.content(), l, user(), "UTC"),
              r.content().size());
  }
  public ExportedReport out(Long c, Boolean a, Locale l) {
    var r = svc.outOfStock(c, a, PageRequest.of(0, props.maxPdfRows() + 1));
    check(r.totalElements());
    return er(
        "inventory-out-of-stock",
        pdf.table(
            "Out of Stock",
            new String[] {"Product", "Category", "Stock", "Last Sold",
                          "Last Movement", "Total Sold"},
            r.content()
                .stream()
                .map(x
                     -> new String[] {
                         x.productName(), x.categoryName(),
                         "" + x.currentStock(), String.valueOf(x.lastSoldAt()),
                         String.valueOf(x.lastInventoryMovementAt()),
                         "" + x.totalHistoricalQuantitySold()})
                .toList(),
            List.of("dateBecameOutOfStock is null unless reliably derivable."),
            l, user(), "UTC"),
        r.content().size());
  }
  public ExportedReport valuation(Long c, Boolean a, StockStatus s, Locale l) {
    var r = svc.valuation(c, a, s, PageRequest.of(0, props.maxPdfRows() + 1));
    check(r.rows().totalElements());
    return er("inventory-valuation",
              pdf.table("Stock Valuation",
                        new String[] {"Product", "Category", "Stock",
                                      "Unit Cost", "Selling Price",
                                      "Stock Value", "Retail Value", "Margin"},
                        r.rows()
                            .content()
                            .stream()
                            .map(x
                                 -> new String[] {
                                     x.productName(), x.categoryName(),
                                     "" + x.currentStock(),
                                     String.valueOf(x.unitCost()),
                                     String.valueOf(x.sellingPrice()),
                                     String.valueOf(x.stockCostValue()),
                                     String.valueOf(x.retailValue()),
                                     String.valueOf(x.potentialGrossMargin())})
                            .toList(),
                        List.of(r.limitation()), l, user(), "UTC"),
              r.rows().content().size());
  }
  public ExportedReport movements(Movement f, Locale l) {
    range(new Period(f.period(), f.dateFrom(), f.dateTo()));
    var r = svc.movements(f, PageRequest.of(0, props.maxPdfRows() + 1));
    check(r.totalElements());
    return er("inventory-movements",
              pdf.movements(r.content(), l, user(), "UTC"), r.content().size());
  }
  public ExportedReport ledger(Long id, Period p, Locale l) {
    range(p);
    var r = svc.ledger(id, p, PageRequest.of(0, props.maxPdfRows() + 1));
    check(r.movements().totalElements());
    return er(
        "inventory-ledger",
        pdf.table("Product Ledger",
                  new String[] {"Date", "Type", "Reference", "Before", "Change",
                                "After", "Performed By"},
                  r.movements()
                      .content()
                      .stream()
                      .map(x
                           -> new String[] {
                               String.valueOf(x.createdAt()),
                               String.valueOf(x.movementType()), x.reference(),
                               "" + x.quantityBefore(), "" + x.quantityChange(),
                               "" + x.quantityAfter(), x.performedBy()})
                      .toList(),
                  List.of("Opening balance: " + r.openingBalance(),
                          "Closing balance: " + r.closingBalance()),
                  l, user(), "UTC"),
        r.movements().content().size());
  }
  public ExportedReport neverSold(Long c, Boolean a, LocalDate b, Locale l) {
    var r = svc.neverSold(c, a, b, PageRequest.of(0, props.maxPdfRows() + 1));
    check(r.totalElements());
    return er(
        "inventory-never-sold",
        pdf.table(
            "Never Sold",
            new String[] {"Product", "Category", "Stock", "Created",
                          "Selling Price", "Stock Value", "Last Movement"},
            r.content()
                .stream()
                .map(x
                     -> new String[] {
                         x.productName(), x.categoryName(),
                         "" + x.currentStock(), String.valueOf(x.createdAt()),
                         String.valueOf(x.sellingPrice()),
                         String.valueOf(x.stockValue()),
                         String.valueOf(x.lastInventoryMovementAt())})
                .toList(),
            List.of(), l, user(), "UTC"),
        r.content().size());
  }
  public ExportedReport adjusted(Period p, int lim, String sort, Locale l) {
    range(p);
    var r = svc.mostAdjusted(p, Math.min(lim, props.maxPdfRows() + 1), sort);
    check(r.size());
    return er(
        "inventory-most-adjusted",
        pdf.table(
            "Most Adjusted",
            new String[] {"Product", "Count", "Increased", "Decreased", "Net",
                          "Last", "Current Stock"},
            r.stream()
                .map(x
                     -> new String[] {x.productName(), "" + x.adjustmentCount(),
                                      "" + x.totalQuantityIncreased(),
                                      "" + x.totalQuantityDecreased(),
                                      "" + x.netAdjustment(),
                                      String.valueOf(x.lastAdjustmentAt()),
                                      "" + x.currentStock()})
                .toList(),
            List.of(), l, user(), "UTC"),
        r.size());
  }
  public ExportedReport
  restorations(Period p, com.smart.ecommerce.enums.InventoryMovementType mt,
               Locale l) {
    range(p);
    var r = svc.restorations(p, mt, PageRequest.of(0, props.maxPdfRows() + 1));
    check(r.rows().totalElements());
    return er("inventory-restorations",
              pdf.table("Inventory Restorations",
                        new String[] {"Product", "Type", "Order", "Customer",
                                      "Qty", "By", "Reason", "Date"},
                        r.rows()
                            .content()
                            .stream()
                            .map(x
                                 -> new String[] {
                                     x.productName(),
                                     String.valueOf(x.movementType()),
                                     x.orderNumber(), x.customerNameSnapshot(),
                                     "" + x.quantityRestored(), x.performedBy(),
                                     x.reason(), String.valueOf(x.createdAt())})
                            .toList(),
                        List.of(), l, user(), "UTC"),
              r.rows().content().size());
  }
  public ExportedReport reconciliation(boolean only, Locale l) {
    var r = svc.reconciliation(only);
    check(r.rows().size());
    return er("inventory-reconciliation",
              pdf.reconciliation(r.rows(), l, user(), "UTC"), r.rows().size());
  }
  private void range(Period p) {
    var r = ranges.resolve(p.period(), p.dateFrom(), p.dateTo());
    if (r.daysInclusive() > props.maxExportRangeDays())
      throw new IllegalArgumentException("report.error.range_too_large");
  }
  private ExportedReport er(String n, byte[] b, int c) {
    return new ExportedReport(n + ".pdf", b, c);
  }
  private String user() {
    var a = SecurityContextHolder.getContext().getAuthentication();
    return a == null ? "system" : a.getName();
  }
}
