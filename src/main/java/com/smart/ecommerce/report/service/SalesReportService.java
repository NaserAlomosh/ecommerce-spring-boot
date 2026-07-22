package com.smart.ecommerce.report.service;
import com.smart.ecommerce.dto.PaginationResponse;
import com.smart.ecommerce.enums.*;
import com.smart.ecommerce.report.dto.*;
import com.smart.ecommerce.report.dto.ReportDtos.*;
import com.smart.ecommerce.report.repository.*;
import com.smart.ecommerce.report.util.*;
import java.math.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly = true)
public class SalesReportService {
  private final SalesReportRepository repo;
  private final SalesReportQueryRepository query;
  private final DateRangeResolver ranges;
  private final ReportProperties props;
  public SalesReportService(SalesReportRepository repo,
                            SalesReportQueryRepository query,
                            DateRangeResolver ranges, ReportProperties props) {
    this.repo = repo;
    this.query = query;
    this.ranges = ranges;
    this.props = props;
  }
  public SalesSummaryResponse summary(ReportPeriod p, LocalDate f,
                                      LocalDate t) {
    var r = ranges.resolve(p, f, t);
    var s = repo.summary(r.startInclusive(), r.endExclusive(),
                         CurrencyCode.DEFAULT_CURRENCY);
    BigDecimal rev = ReportMath.money(s.getTotalRevenue());
    long orders = s.getCompletedOrders();
    long items = s.getTotalItemsSold();
    long newC = repo.newCustomers(r.startInclusive(), r.endExclusive());
    return new SalesSummaryResponse(
        r.dateFrom(), r.dateTo(), CurrencyCode.DEFAULT_CURRENCY, rev, orders,
        items, ReportMath.divide(rev, orders), s.getUniqueCustomers(),
        ReportMath.divide(BigDecimal.valueOf(items), orders),
        ReportMath.money(s.getHighestOrderValue()),
        ReportMath.money(s.getLowestOrderValue()), newC,
        Math.max(0, s.getUniqueCustomers() - newC));
  }
  public SalesComparisonResponse comparison(ReportPeriod p, LocalDate f,
                                            LocalDate t) {
    var c = ranges.resolve(p, f, t);
    var prev = ranges.previous(c);
    var cs = summaryFor(c);
    var ps = summaryFor(prev);
    var rc = ReportMath.change(cs.revenue, ps.revenue);
    var oc = ReportMath.change(BigDecimal.valueOf(cs.orders),
                               BigDecimal.valueOf(ps.orders));
    var ic = ReportMath.change(BigDecimal.valueOf(cs.items),
                               BigDecimal.valueOf(ps.items));
    var ac = ReportMath.change(ReportMath.divide(cs.revenue, cs.orders),
                               ReportMath.divide(ps.revenue, ps.orders));
    return new SalesComparisonResponse(
        c.response(), prev.response(), cs.revenue, ps.revenue, rc.amount(),
        rc.percentage(), rc.growthState(), cs.orders, ps.orders,
        cs.orders - ps.orders, oc.percentage(), oc.growthState(), cs.items,
        ps.items, ic.percentage(), ic.growthState(),
        ReportMath.divide(cs.revenue, cs.orders),
        ReportMath.divide(ps.revenue, ps.orders), ac.percentage(),
        ac.growthState());
  }
  private Mini summaryFor(DateRangeResolver.ResolvedDateRange r) {
    var s = repo.summary(r.startInclusive(), r.endExclusive(),
                         CurrencyCode.DEFAULT_CURRENCY);
    return new Mini(ReportMath.money(s.getTotalRevenue()),
                    s.getCompletedOrders(), s.getTotalItemsSold());
  }
  record Mini(BigDecimal revenue, long orders, long items) {}
  public SalesTrendResponse trend(ReportPeriod p, LocalDate f, LocalDate t,
                                  ReportGranularity g) {
    var r = ranges.resolve(p, f, t);
    validateGranularity(r, g);
    return new SalesTrendResponse(
        r.dateFrom(), r.dateTo(), g,
        fill(r, g,
             query.trend(r.startInclusive(), r.endExclusive(), g, r.zoneId(),
                         null)));
  }
  public OrderStatusSummaryResponse statusSummary(ReportPeriod p, LocalDate f,
                                                  LocalDate t) {
    var r = ranges.resolve(p, f, t);
    Map<OrderStatus, Long> m = new EnumMap<>(OrderStatus.class);
    repo.statusSummary(r.startInclusive(), r.endExclusive())
        .forEach(x -> m.put(x.getStatus(), x.getTotal()));
    long total = m.values().stream().mapToLong(Long::longValue).sum();
    return new OrderStatusSummaryResponse(
        total, c(m, OrderStatus.PENDING), c(m, OrderStatus.PROCESSING),
        c(m, OrderStatus.OUT_FOR_DELIVERY), c(m, OrderStatus.COMPLETED),
        c(m, OrderStatus.FAILED), c(m, OrderStatus.CANCELLED),
        rate(c(m, OrderStatus.COMPLETED), total),
        rate(c(m, OrderStatus.CANCELLED), total),
        rate(c(m, OrderStatus.FAILED), total));
  }
  long c(Map<OrderStatus, Long> m, OrderStatus s) {
    return m.getOrDefault(s, 0L);
  }
  BigDecimal rate(long a, long b) {
    return b == 0 ? ReportMath.money(BigDecimal.ZERO)
                  : ReportMath.money(BigDecimal.valueOf(a * 100).divide(
                        BigDecimal.valueOf(b), 2, RoundingMode.HALF_UP));
  }
  public PaginationResponse<SalesOrderRow> orders(SalesOrderFilter flt,
                                                  Pageable pg) {
    var r = ranges.resolve(flt.period(), flt.dateFrom(), flt.dateTo());
    return PaginationResponse.from(
        repo.orderRows(r.startInclusive(), r.endExclusive(),
                       blank(flt.orderNumber()), flt.customerId(),
                       blank(flt.customerName()), flt.minAmount(),
                       flt.maxAmount(), flt.productId(), flt.categoryId(),
                       flt.assignedDeliveryUserId(),
                       CurrencyCode.normalizeNullable(flt.currency()), pg)
            .map(this::row));
  }
  public List<TopProductResponse> topProducts(ReportPeriod p, LocalDate f,
                                              LocalDate t, Integer limit,
                                              String sortBy) {
    var r = ranges.resolve(p, f, t);
    int l = Math.min(limit == null ? 10 : limit, 100);
    if (l < 1)
      throw new IllegalArgumentException("report.error.invalid_limit");
    Sort sort = switch (sortBy == null ? "QUANTITY_SOLD" : sortBy) {
      case "REVENUE" -> Sort.by(Sort.Direction.DESC, "revenue");
      case "ORDERS_COUNT" -> Sort.by(Sort.Direction.DESC, "ordersCount");
      case "AVERAGE_RATING" -> Sort.by(Sort.Direction.DESC, "averageRating");
      default -> Sort.by(Sort.Direction.DESC, "quantitySold");
    };
    return repo
        .topProducts(r.startInclusive(), r.endExclusive(),
                     PageRequest.of(0, l, sort))
        .stream()
        .map(this::top)
        .toList();
  }
  public PaginationResponse<CategorySalesResponse>
  categories(ReportPeriod p, LocalDate f, LocalDate t, Pageable pg) {
    var r = ranges.resolve(p, f, t);
    BigDecimal total = summaryFor(r).revenue;
    return PaginationResponse.from(
        repo.categories(r.startInclusive(), r.endExclusive(), pg)
            .map(x
                 -> new CategorySalesResponse(
                     x.getCategoryId(), x.getCategoryName(),
                     x.getProductsSold(), x.getQuantitySold(),
                     x.getCompletedOrders(), ReportMath.money(x.getRevenue()),
                     total.signum() == 0
                         ? ReportMath.money(BigDecimal.ZERO)
                         : ReportMath.money(
                               x.getRevenue()
                                   .multiply(BigDecimal.valueOf(100))
                                   .divide(total, 2, RoundingMode.HALF_UP)),
                     ReportMath.divide(x.getRevenue(), x.getItemRows()),
                     "Order items do not store category snapshots; category " +
                     "reporting uses the current product-category relation " +
                     "when the product still exists.")));
  }
  public PaginationResponse<CustomerSalesResponse>
  customers(CustomerSalesFilter flt, Pageable pg) {
    var r = ranges.resolve(flt.period(), flt.dateFrom(), flt.dateTo());
    return PaginationResponse.from(
        repo.customers(r.startInclusive(), r.endExclusive(), flt.customerId(),
                       blank(flt.customerName()), pg)
            .map(x
                 -> new CustomerSalesResponse(
                     x.getCustomerId(), x.getCustomerName(),
                     x.getCompletedOrders(), x.getItemsPurchased(),
                     ReportMath.money(x.getTotalSpent()),
                     ReportMath.divide(x.getTotalSpent(),
                                       x.getCompletedOrders()),
                     x.getFirstCompletedOrderAt(), x.getLastCompletedOrderAt(),
                     classify(x.getTotalSpent(), x.getCompletedOrders()))));
  }
  public ProductSalesResponse product(Long id, ReportPeriod p, LocalDate f,
                                      LocalDate t, ReportGranularity g) {
    if (!repo.existsById(id))
      throw new IllegalArgumentException("report.error.product_not_found");
    var r = ranges.resolve(p, f, t);
    validateGranularity(r, g);
    var info = repo.productInfo(id);
    var rows =
        query.trend(r.startInclusive(), r.endExclusive(), g, r.zoneId(), id);
    long qty = rows.stream()
                   .mapToLong(SalesReportQueryRepository.TrendRow::itemsSold)
                   .sum();
    long ord = rows.stream()
                   .mapToLong(SalesReportQueryRepository.TrendRow::ordersCount)
                   .sum();
    BigDecimal rev = rows.stream()
                         .map(SalesReportQueryRepository.TrendRow::revenue)
                         .reduce(BigDecimal.ZERO, BigDecimal::add);
    return new ProductSalesResponse(
        id, info.getProductName(), info.getSku(), info.getCategoryId(),
        info.getCategoryName(), ReportMath.money(rev), qty, ord,
        ReportMath.divide(rev, qty), info.getCurrentStock(),
        ReportMath.money(info.getAverageRating()), info.getReviewsCount(),
        fill(r, g, rows),
        repo.recentItems(id, r.startInclusive(), r.endExclusive(),
                         PageRequest.of(0, 20))
            .stream()
            .map(x
                 -> new RecentProductOrderItem(
                     x.getOrderNumber(), x.getCustomerName(),
                     x.getCompletedAt(), x.getQuantity(),
                     ReportMath.money(x.getUnitPrice()),
                     ReportMath.money(x.getLineTotal()), x.getCurrency()))
            .toList());
  }
  private List<TrendPoint>
  fill(DateRangeResolver.ResolvedDateRange r, ReportGranularity g,
       List<SalesReportQueryRepository.TrendRow> rows) {
    Map<String, SalesReportQueryRepository.TrendRow> m = new HashMap<>();
    rows.forEach(x -> m.put(x.label(), x));
    List<TrendPoint> out = new ArrayList<>();
    ZonedDateTime z = r.dateFrom().atStartOfDay(r.zoneId());
    ZonedDateTime end = r.dateTo().plusDays(1).atStartOfDay(r.zoneId());
    while (z.isBefore(end)) {
      String label = label(z, g);
      var x = m.get(label);
      BigDecimal rev = x == null ? BigDecimal.ZERO : x.revenue();
      long o = x == null ? 0 : x.ordersCount(),
           items = x == null ? 0 : x.itemsSold();
      out.add(new TrendPoint(label, z.toInstant(), ReportMath.money(rev), o,
                             items, ReportMath.divide(rev, o)));
      z = advance(z, g);
    }
    return out;
  }
  private ZonedDateTime advance(ZonedDateTime z, ReportGranularity g) {
    return switch (g) {
      case HOUR -> z.plusHours(1);
      case DAY -> z.plusDays(1);
      case WEEK -> z.plusWeeks(1);
      case MONTH -> z.plusMonths(1);
      case YEAR -> z.plusYears(1);
    };
  }
  private String label(ZonedDateTime z, ReportGranularity g) {
    return switch (g) {
      case HOUR ->
        z.toLocalDate() + " " + String.format("%02d:00", z.getHour());
      case DAY -> z.toLocalDate().toString();
      case WEEK ->
        z.get(IsoFields.WEEK_BASED_YEAR) + "-W" +
            String.format("%02d", z.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
      case MONTH -> String.format("%04d-%02d", z.getYear(), z.getMonthValue());
      case YEAR -> String.valueOf(z.getYear());
    };
  }
  private void validateGranularity(DateRangeResolver.ResolvedDateRange r,
                                   ReportGranularity g) {
    if (g == null)
      throw new IllegalArgumentException(
          "report.error.unsupported_granularity");
    long d = r.daysInclusive();
    if (g == ReportGranularity.HOUR && d > 3)
      throw new IllegalArgumentException(
          "report.error.unsupported_granularity");
    if (g == ReportGranularity.DAY && d > 366)
      throw new IllegalArgumentException(
          "report.error.unsupported_granularity");
  }
  private String classify(BigDecimal spent, long orders) {
    return spent.compareTo(props.vipMinSpending()) >= 0 ||
            orders >= props.vipMinOrders()
        ? "VIP"
    : orders <= 1 ? "NEW"
                  : "RETURNING";
  }
  private String blank(String s) {
    return s == null || s.isBlank() ? null : s.trim();
  }
  private SalesOrderRow row(SalesReportRepository.OrderRowProjection x) {
    return new SalesOrderRow(
        x.getOrderId(), x.getOrderNumber(), x.getCustomerId(),
        x.getCustomerName(), ReportMath.money(x.getTotalAmount()),
        x.getCurrency(), x.getItemsCount(), x.getTotalQuantity(),
        x.getCompletedAt(), x.getCreatedAt(),
        x.getDeliveryUserId() == null
            ? null
            : new DeliveryUserSummary(x.getDeliveryUserId(),
                                      x.getDeliveryUserName()));
  }
  private TopProductResponse top(SalesReportRepository.TopProductProjection x) {
    return new TopProductResponse(
        x.getProductId(), x.getProductName(), x.getSku(), x.getCategoryId(),
        x.getCategoryName(), x.getQuantitySold(), x.getOrdersCount(),
        ReportMath.money(x.getRevenue()),
        ReportMath.divide(x.getRevenue(), x.getQuantitySold()),
        x.getCurrentStock(), ReportMath.money(x.getAverageRating()),
        x.getReviewsCount());
  }
}
