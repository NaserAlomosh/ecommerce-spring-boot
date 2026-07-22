package com.smart.ecommerce.report.service;
import com.smart.ecommerce.dto.PaginationResponse;
import com.smart.ecommerce.enums.InventoryMovementType;
import com.smart.ecommerce.report.dto.InventoryFilters.*;
import com.smart.ecommerce.report.dto.InventoryReportDtos.*;
import com.smart.ecommerce.report.repository.InventoryReportQueryRepository;
import com.smart.ecommerce.report.util.*;
import java.math.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly = true)
public class InventoryReportService {
  private final InventoryReportQueryRepository q;
  private final DateRangeResolver ranges;
  public InventoryReportService(InventoryReportQueryRepository q,
                                DateRangeResolver ranges) {
    this.q = q;
    this.ranges = ranges;
  }
  public PaginationResponse<CurrentInventoryRow> current(Current f,
                                                         Pageable p) {
    return PaginationResponse.from(
        q.current(f.productId(), f.productName(), f.categoryId(), f.active(),
                  f.stockStatus(), f.minStock(), f.maxStock(), p));
  }
  public PaginationResponse<LowStockRow> lowStock(Long cid, Integer threshold,
                                                  Boolean active, Period per,
                                                  Pageable p) {
    var r = ranges.resolve(per.period(), per.dateFrom(), per.dateTo());
    return PaginationResponse.from(q.low(cid, active, threshold,
                                         r.startInclusive(), r.endExclusive(),
                                         r.daysInclusive(), p));
  }
  public PaginationResponse<OutOfStockRow> outOfStock(Long cid, Boolean active,
                                                      Pageable p) {
    var page = q.current(null, null, cid, active, StockStatus.OUT_OF_STOCK,
                         null, null, p);
    return PaginationResponse.from(page.map(
        x
        -> new OutOfStockRow(x.productId(), x.productName(), x.categoryName(),
                             x.currentStock(), lastSold(x.productId()),
                             x.lastInventoryMovementAt(),
                             soldAll(x.productId()), null, x.averageRating(),
                             x.reviewsCount())));
  }
  public ValuationResponse valuation(Long cid, Boolean active, StockStatus st,
                                     Pageable p) {
    var rows =
        current(
            new Current(null, null, cid, active, st, null, null, null, null), p)
            .content()
            .stream()
            .map(x
                 -> new ValuationRow(x.productId(), x.productName(),
                                     x.categoryName(), x.currentStock(), null,
                                     x.sellingPrice(), null,
                                     x.potentialRetailValue(), null, null))
            .toList();
    long units = rows.stream().mapToLong(ValuationRow::currentStock).sum();
    BigDecimal retail = rows.stream()
                            .map(ValuationRow::retailValue)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return new ValuationResponse(
        rows.size(), units, null, retail, null, "JOD", List.of(),
        new PaginationResponse<>(rows, p.getPageNumber(), p.getPageSize(),
                                 rows.size(), 1, true, true),
        "Product has no unit-cost field; cost valuation and margin are " +
        "intentionally null.");
  }
  public PaginationResponse<MovementRow> movements(Movement f, Pageable p) {
    Map<String, Object> m = new HashMap<>();
    String e = "";
    if (f.productId() != null) {
      e += " and h.product_id=:pid";
      m.put("pid", f.productId());
    }
    if (f.categoryId() != null) {
      e += " and p.category_id=:cid";
      m.put("cid", f.categoryId());
    }
    if (f.movementType() != null) {
      e += " and h.movement_type=:mt";
      m.put("mt", f.movementType().name());
    }
    if (f.orderNumber() != null) {
      e += " and lower(h.order_number) like :on";
      m.put("on", "%" + f.orderNumber().toLowerCase() + "%");
    }
    if (f.customerId() != null) {
      e += " and h.customer_id=:cust";
      m.put("cust", f.customerId());
    }
    if (f.performedByUserId() != null) {
      e += " and h.performed_by_id=:pb";
      m.put("pb", f.performedByUserId());
    }
    var r = ranges.resolve(f.period(), f.dateFrom(), f.dateTo());
    e += " and h.movement_at>=:from and h.movement_at<:to";
    m.put("from", Timestamp.from(r.startInclusive()));
    m.put("to", Timestamp.from(r.endExclusive()));
    if (f.minQuantityChange() != null) {
      e += " and h.quantity_change>=:minq";
      m.put("minq", f.minQuantityChange());
    }
    if (f.maxQuantityChange() != null) {
      e += " and h.quantity_change<=:maxq";
      m.put("maxq", f.maxQuantityChange());
    }
    if (f.direction() != null)
      e += switch (f.direction()) {
        case INCREASE -> " and h.quantity_change>0";
        case DECREASE -> " and h.quantity_change<0";
        case ZERO -> " and h.quantity_change=0";
      };
    return PaginationResponse.from(q.movements(e, m, p));
  }
  public MovementSummaryResponse movementSummary(Period p, Long pid, Long cid) {
    var f = new Movement(pid, cid, null, null, null, null, p.period(),
                         p.dateFrom(), p.dateTo(), null, null, null);
    var rows = movements(f, PageRequest.of(0, 100000)).content();
    Map<InventoryMovementType, Long> by =
        new EnumMap<>(InventoryMovementType.class);
    rows.forEach(x -> by.merge(x.movementType(), 1L, Long::sum));
    return new MovementSummaryResponse(
        rows.size(),
        rows.stream()
            .filter(x -> x.quantityChange() > 0)
            .mapToLong(MovementRow::quantityChange)
            .sum(),
        Math.abs(rows.stream()
                     .filter(x -> x.quantityChange() < 0)
                     .mapToLong(MovementRow::quantityChange)
                     .sum()),
        rows.stream().mapToLong(MovementRow::quantityChange).sum(), by,
        rows.stream().map(MovementRow::productId).distinct().count(),
        rows.stream()
            .map(MovementRow::orderId)
            .filter(Objects::nonNull)
            .distinct()
            .count(),
        rows.stream()
            .map(MovementRow::customerId)
            .filter(Objects::nonNull)
            .distinct()
            .count());
  }
  public ProductInventoryLedgerResponse ledger(Long productId, Period per,
                                               Pageable p) {
    var r = ranges.resolve(per.period(), per.dateFrom(), per.dateTo());
    var page = movements(new Movement(productId, null, null, null, null, null,
                                      per.period(), per.dateFrom(),
                                      per.dateTo(), null, null, null),
                         p);
    Integer open = toInt(scalar(
        "select quantity_before from inventory_history where product_id=:id " +
        "and movement_at>=:from order by movement_at asc,id asc limit 1",
        Map.of("id", productId, "from", Timestamp.from(r.startInclusive()))));
    Integer close = toInt(scalar(
        "select quantity_after from inventory_history where product_id=:id " +
        "and movement_at<:to order by movement_at desc,id desc limit 1",
        Map.of("id", productId, "to", Timestamp.from(r.endExclusive()))));
    var info = (Object[])q.one(
        "select p.name_en,p.sku,c.name_en,p.stock_quantity from products p " +
        "join categories c on c.id=p.category_id where p.id=:id",
        Map.of("id", productId));
    var rows = page.content()
                   .stream()
                   .map(x
                        -> new LedgerMovementRow(
                            x.createdAt(), x.movementType(), x.orderNumber(),
                            x.quantityBefore(), x.quantityChange(),
                            x.quantityAfter(), x.performedByName(), x.note()))
                   .toList();
    return new ProductInventoryLedgerResponse(
        productId, (String)info[0], (String)info[1], (String)info[2],
        ((Number)info[3]).intValue(), open, close,
        rows.stream()
            .filter(x -> x.quantityChange() > 0)
            .mapToLong(LedgerMovementRow::quantityChange)
            .sum(),
        Math.abs(rows.stream()
                     .filter(x -> x.quantityChange() < 0)
                     .mapToLong(LedgerMovementRow::quantityChange)
                     .sum()),
        rows.stream().mapToLong(LedgerMovementRow::quantityChange).sum(),
        new PaginationResponse<>(rows, page.page(), page.size(),
                                 page.totalElements(), page.totalPages(),
                                 page.first(), page.last()));
  }
  public PaginationResponse<NeverSoldRow>
  neverSold(Long cid, Boolean active, LocalDate before, Pageable p) {
    var cur = current(
        new Current(null, null, cid, active, null, null, null, null, null), p);
    Instant now = Instant.now();
    return new PaginationResponse<>(
        cur.content()
            .stream()
            .filter(x -> soldAll(x.productId()) == 0)
            .map(x
                 -> new NeverSoldRow(
                     x.productId(), x.productName(), x.categoryName(),
                     x.currentStock(), null, 0, x.sellingPrice(),
                     x.potentialRetailValue(), x.lastInventoryMovementAt()))
            .toList(),
        cur.page(), cur.size(), cur.totalElements(), cur.totalPages(),
        cur.first(), cur.last());
  }
  public List<MostAdjustedRow> mostAdjusted(Period p, int limit, String sort) {
    var rows =
        movements(new Movement(null, null, null, null, null, null, p.period(),
                               p.dateFrom(), p.dateTo(), null, null, null),
                  PageRequest.of(0, Math.max(limit, 1)))
            .content()
            .stream()
            .filter(x
                    -> x.movementType() ==
                               InventoryMovementType.ADMIN_ADJUSTMENT ||
                           x.movementType() ==
                               InventoryMovementType.PRODUCT_UPDATED)
            .toList();
    Map<Long, List<MovementRow>> g = new HashMap<>();
    rows.forEach(
        x -> g.computeIfAbsent(x.productId(), k -> new ArrayList<>()).add(x));
    return g.entrySet()
        .stream()
        .map(e -> {
          var l = e.getValue();
          var first = l.get(0);
          return new MostAdjustedRow(
              e.getKey(), first.productName(), l.size(),
              l.stream()
                  .filter(x -> x.quantityChange() > 0)
                  .mapToLong(MovementRow::quantityChange)
                  .sum(),
              Math.abs(l.stream()
                           .filter(x -> x.quantityChange() < 0)
                           .mapToLong(MovementRow::quantityChange)
                           .sum()),
              l.stream().mapToLong(MovementRow::quantityChange).sum(),
              l.stream()
                  .map(MovementRow::createdAt)
                  .max(Comparator.naturalOrder())
                  .orElse(null),
              first.quantityAfter());
        })
        .limit(limit)
        .toList();
  }
  public RestorationResponse restorations(Period p, InventoryMovementType mt,
                                          Pageable pg) {
    if (mt != null && mt != InventoryMovementType.ORDER_CANCELLED &&
        mt != InventoryMovementType.PRODUCT_RETURNED)
      throw new IllegalArgumentException(
          "report.inventory.error.unsupported_movement_type");
    var rows = movements(new Movement(null, null, mt, null, null, null,
                                      p.period(), p.dateFrom(), p.dateTo(), 1,
                                      null, MovementDirection.INCREASE),
                         pg)
                   .content()
                   .stream()
                   .filter(x
                           -> x.movementType() ==
                                      InventoryMovementType.ORDER_CANCELLED ||
                                  x.movementType() ==
                                      InventoryMovementType.PRODUCT_RETURNED)
                   .map(x
                        -> new RestorationRow(
                            x.productId(), x.productName(), x.movementType(),
                            x.orderNumber(), x.customerNameSnapshot(),
                            x.quantityChange(), x.performedByName(), x.note(),
                            x.createdAt()))
                   .toList();
    long c = rows.stream()
                 .filter(x
                         -> x.movementType() ==
                                InventoryMovementType.ORDER_CANCELLED)
                 .mapToLong(RestorationRow::quantityRestored)
                 .sum(),
         ret = rows.stream()
                   .filter(x
                           -> x.movementType() ==
                                  InventoryMovementType.PRODUCT_RETURNED)
                   .mapToLong(RestorationRow::quantityRestored)
                   .sum();
    return new RestorationResponse(
        rows.size(), c + ret, c, ret,
        new PaginationResponse<>(rows, pg.getPageNumber(), pg.getPageSize(),
                                 rows.size(), 1, true, true));
  }
  public ReconciliationResponse reconciliation(boolean only) {
    var rows = new ArrayList<ReconciliationRow>();
    for (Object o :
         q.list("select p.id,p.name_en,p.stock_quantity,(select " +
                "h.quantity_after from inventory_history h where " +
                "h.product_id=p.id order by h.movement_at desc,h.id desc " +
                "limit 1),(select max(h.movement_at) from inventory_history " +
                "h where h.product_id=p.id) from products p where p.deleted=0",
                Map.of())) {
      Object[] a = (Object[])o;
      Integer d = a[3] == null ? null : ((Number)a[3]).intValue();
      int s = ((Number)a[2]).intValue();
      var st = d == null ? ReconciliationStatus.NO_HISTORY
               : d == s  ? ReconciliationStatus.MATCHED
                         : ReconciliationStatus.MISMATCHED;
      if (!only || st != ReconciliationStatus.MATCHED)
        rows.add(new ReconciliationRow(
            ((Number)a[0]).longValue(), (String)a[1], s, d,
            d == null ? null : s - d, st,
            a[4] == null ? null : ((Timestamp)a[4]).toInstant()));
    }
    return new ReconciliationResponse(
        rows.size(),
        rows.stream()
            .filter(
                x -> x.reconciliationStatus() == ReconciliationStatus.MATCHED)
            .count(),
        rows.stream()
            .filter(x
                    -> x.reconciliationStatus() ==
                           ReconciliationStatus.MISMATCHED)
            .count(),
        rows.stream()
            .filter(x
                    -> x.reconciliationStatus() ==
                           ReconciliationStatus.NO_HISTORY)
            .count(),
        rows.stream()
            .mapToLong(
                x -> x.difference() == null ? 0 : Math.abs(x.difference()))
            .sum(),
        rows);
  }
  public InventoryTrendResponse trend(Period p, ReportGranularity g, Long pid,
                                      Long cid) {
    var r = ranges.resolve(p.period(), p.dateFrom(), p.dateTo());
    var rows =
        movements(new Movement(pid, cid, null, null, null, null, p.period(),
                               p.dateFrom(), p.dateTo(), null, null, null),
                  PageRequest.of(0, 100000))
            .content();
    Map<String, List<MovementRow>> m = new HashMap<>();
    rows.forEach(
        x
        -> m.computeIfAbsent(
                label(ZonedDateTime.ofInstant(x.createdAt(), r.zoneId()), g),
                k -> new ArrayList<>())
               .add(x));
    List<InventoryTrendPoint> pts = new ArrayList<>();
    for (ZonedDateTime z = r.dateFrom().atStartOfDay(r.zoneId()),
                       end = r.dateTo().plusDays(1).atStartOfDay(r.zoneId());
         z.isBefore(end); z = adv(z, g)) {
      String lab = label(z, g);
      var l = m.getOrDefault(lab, List.of());
      pts.add(new InventoryTrendPoint(
          z.toInstant(), lab,
          l.stream()
              .filter(x -> x.quantityChange() > 0)
              .mapToLong(MovementRow::quantityChange)
              .sum(),
          Math.abs(l.stream()
                       .filter(x -> x.quantityChange() < 0)
                       .mapToLong(MovementRow::quantityChange)
                       .sum()),
          l.stream().mapToLong(MovementRow::quantityChange).sum(), l.size()));
    }
    return new InventoryTrendResponse(r.dateFrom(), r.dateTo(), g, pts);
  }
  public TurnoverResponse turnover(Period p, Long pid, Long cid) {
    var s = movementSummary(p, pid, cid);
    return new TurnoverResponse(
        p.dateFrom(), p.dateTo(), pid, cid, s.totalQuantityDecreased(), null,
        null, null,
        "Operational turnover only; average historical units on hand is not " +
        "materialized as daily snapshots.");
  }
  private Object scalar(String sql, Map<String, Object> p) {
    try {
      return q.one(sql, p);
    } catch (Exception e) {
      return null;
    }
  }
  private Integer toInt(Object v) {
    return v == null ? null : ((Number)v).intValue();
  }
  private long soldAll(Long id) {
    return ((Number)q.one("select coalesce(sum(-quantity_change),0) from " +
                          "inventory_history where product_id=:id and " +
                          "movement_type='ORDER_CREATED'",
                          Map.of("id", id)))
        .longValue();
  }
  private Instant lastSold(Long id) {
    Object v = scalar("select max(movement_at) from inventory_history where " +
                      "product_id=:id and movement_type='ORDER_CREATED'",
                      Map.of("id", id));
    return v == null ? null : ((Timestamp)v).toInstant();
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
  private ZonedDateTime adv(ZonedDateTime z, ReportGranularity g) {
    return switch (g) {
      case HOUR -> z.plusHours(1);
      case DAY -> z.plusDays(1);
      case WEEK -> z.plusWeeks(1);
      case MONTH -> z.plusMonths(1);
      case YEAR -> z.plusYears(1);
    };
  }
}
