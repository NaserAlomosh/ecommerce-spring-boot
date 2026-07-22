package com.smart.ecommerce.report.service;
import com.smart.ecommerce.dto.PaginationResponse;
import com.smart.ecommerce.report.dto.*;
import com.smart.ecommerce.report.dto.ReportDtos.*;
import com.smart.ecommerce.report.generator.pdf.PdfSalesReportGenerator;
import com.smart.ecommerce.report.util.ReportPeriod;
import java.time.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
@Service
public class ReportExportService {
  private final SalesReportService sales;
  private final CustomerReviewReportService cr;
  private final DateRangeResolver ranges;
  private final ReportProperties props;
  private final PdfSalesReportGenerator pdf;
  public ReportExportService(SalesReportService sales,
                             CustomerReviewReportService cr,
                             DateRangeResolver ranges, ReportProperties props,
                             PdfSalesReportGenerator pdf) {
    this.sales = sales;
    this.cr = cr;
    this.ranges = ranges;
    this.props = props;
    this.pdf = pdf;
  }
  public ExportedReport salesPdf(SalesOrderFilter f, Locale l) {
    checkRange(f.period(), f.dateFrom(), f.dateTo());
    var summary = sales.summary(f.period(), f.dateFrom(), f.dateTo());
    var rows = sales.orders(
        f, PageRequest.of(0, props.maxPdfRows() + 1,
                          Sort.by(Sort.Direction.DESC, "completedAt")));
    checkRows(rows.totalElements());
    return new ExportedReport(
        name("sales-report", summary.dateFrom(), summary.dateTo()),
        pdf.sales(summary, rows, l, user(),
                  zone(f.period(), f.dateFrom(), f.dateTo())),
        rows.content().size());
  }
  public ExportedReport topProductsPdf(ReportPeriod p, LocalDate f, LocalDate t,
                                       Integer limit, String sort, Locale l) {
    checkRange(p, f, t);
    var rows = sales.topProducts(
        p, f, t, limit == null ? 100 : Math.min(limit, props.maxPdfRows() + 1),
        sort);
    checkRows(rows.size());
    var r = ranges.resolve(p, f, t);
    return new ExportedReport(
        name("top-products-report", r.dateFrom(), r.dateTo()),
        pdf.topProducts(rows, l, user(), r.zoneId().toString()), rows.size());
  }
  public ExportedReport categoriesPdf(ReportPeriod p, LocalDate f, LocalDate t,
                                      Pageable pg, Locale l) {
    checkRange(p, f, t);
    var rows = sales.categories(
        p, f, t, PageRequest.of(0, props.maxPdfRows() + 1, pg.getSort()));
    checkRows(rows.totalElements());
    var r = ranges.resolve(p, f, t);
    return new ExportedReport(
        name("category-sales-report", r.dateFrom(), r.dateTo()),
        pdf.categories(rows, l, user(), r.zoneId().toString()),
        rows.content().size());
  }
  public ExportedReport customersPdf(CustomerSalesFilter f, Pageable pg,
                                     Locale l) {
    checkRange(f.period(), f.dateFrom(), f.dateTo());
    var rows = sales.customers(
        f, PageRequest.of(0, props.maxPdfRows() + 1, pg.getSort()));
    checkRows(rows.totalElements());
    var r = ranges.resolve(f.period(), f.dateFrom(), f.dateTo());
    return new ExportedReport(
        name("customer-sales-report", r.dateFrom(), r.dateTo()),
        pdf.customers(rows, l, user(), r.zoneId().toString()),
        rows.content().size());
  }

  public ExportedReport customerSummaryPdf(Locale l) {
    var s = cr.customerSummary(null);
    return new ExportedReport(
        "customer-summary-report.pdf",
        pdf.customerSummary(s, l, user(), ZoneId.systemDefault().toString()),
        1);
  }
  public ExportedReport topCustomersPdf(ReportPeriod p, LocalDate f,
                                        LocalDate t, Integer limit, String sort,
                                        String name, Boolean active, Locale l) {
    checkRange(p, f, t);
    var rows = cr.top(
        p, f, t, limit == null ? 100 : Math.min(limit, props.maxPdfRows() + 1),
        sort, name, active);
    checkRows(rows.size());
    var r = ranges.resolve(p, f, t);
    return new ExportedReport(
        name("top-customers-report", r.dateFrom(), r.dateTo()),
        pdf.topCustomers(rows, l, user(), r.zoneId().toString()), rows.size());
  }
  public ExportedReport customerHistoryPdf(Long id, ReportPeriod p, LocalDate f,
                                           LocalDate t, Pageable pg, Locale l) {
    checkRange(p, f, t);
    var r = ranges.resolve(p, f, t);
    var h = cr.history(id, p, f, t,
                       PageRequest.of(0, props.maxPdfRows() + 1, pg.getSort()));
    checkRows(h.orders().totalElements());
    return new ExportedReport(
        name("customer-history-report", r.dateFrom(), r.dateTo()),
        pdf.customerHistory(h, l, user(), r.zoneId().toString()),
        h.orders().content().size());
  }
  public ExportedReport reviewsSummaryPdf(Locale l) {
    var s = cr.reviewsSummary(null);
    return new ExportedReport(
        "reviews-summary-report.pdf",
        pdf.reviewsSummary(s, l, user(), ZoneId.systemDefault().toString()), 1);
  }
  public ExportedReport ratedProductsPdf(ReportPeriod p, LocalDate f,
                                         LocalDate t, Integer limit,
                                         String name, boolean lowest,
                                         Locale l) {
    checkRange(p, f, t);
    var rows = cr.rated(
        p, f, t, limit == null ? 100 : Math.min(limit, props.maxPdfRows() + 1),
        name, lowest);
    checkRows(rows.size());
    var r = ranges.resolve(p, f, t);
    return new ExportedReport(name((lowest ? "lowest-rated-products-report"
                                           : "top-rated-products-report"),
                                   r.dateFrom(), r.dateTo()),
                              pdf.ratedProducts(lowest ? "Lowest Rated Products"
                                                       : "Top Rated Products",
                                                rows, l, user(),
                                                r.zoneId().toString()),
                              rows.size());
  }
  public ExportedReport mostReviewedPdf(String name, Pageable pg, Locale l) {
    var rows =
        cr.most(name, PageRequest.of(0, props.maxPdfRows() + 1, pg.getSort()));
    checkRows(rows.totalElements());
    return new ExportedReport(
        "most-reviewed-products-report.pdf",
        pdf.mostReviewed(rows, l, user(), ZoneId.systemDefault().toString()),
        rows.content().size());
  }
  public ExportedReport noReviewsPdf(Long cat, String name, Pageable pg,
                                     Locale l) {
    var rows = cr.noReviews(
        cat, name, PageRequest.of(0, props.maxPdfRows() + 1, pg.getSort()));
    checkRows(rows.totalElements());
    return new ExportedReport(
        "products-without-reviews-report.pdf",
        pdf.noReviews(rows, l, user(), ZoneId.systemDefault().toString()),
        rows.content().size());
  }
  private void checkRange(ReportPeriod p, LocalDate f, LocalDate t) {
    var r = ranges.resolve(p, f, t);
    if (r.daysInclusive() > props.maxExportRangeDays())
      throw new IllegalArgumentException("report.error.range_too_large");
  }
  private void checkRows(long n) {
    if (n > props.maxPdfRows())
      throw new IllegalArgumentException("report.error.pdf_row_limit_exceeded");
  }
  private String name(String prefix, LocalDate f, LocalDate t) {
    return (prefix + "-" + f + "-to-" + t + ".pdf")
        .replaceAll("[^A-Za-z0-9._-]", "-");
  }
  private String user() {
    var a = SecurityContextHolder.getContext().getAuthentication();
    return a == null ? "system" : a.getName();
  }
  private String zone(ReportPeriod p, LocalDate f, LocalDate t) {
    return ranges.resolve(p, f, t).zoneId().toString();
  }
}
