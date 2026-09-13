package com.smart.ecommerce.report.repository;
import com.smart.ecommerce.enums.InventoryMovementType;
import com.smart.ecommerce.report.dto.InventoryReportDtos.*;
import com.smart.ecommerce.report.util.ReportGranularity;
import jakarta.persistence.*;
import java.math.*;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
@Repository
public class InventoryReportQueryRepository {
  private final EntityManager em;
  public InventoryReportQueryRepository(EntityManager em) { this.em = em; }
  private String pn() { return "coalesce(p.name_en,'Historical/Deleted')"; }
  private String cn() { return "coalesce(c.name_en,'Historical/Deleted')"; }
  public Page<CurrentInventoryRow> current(Long pid, String name, Long cid,
                                           Boolean active, StockStatus st,
                                           Integer min, Integer max,
                                           Pageable pg) {
    String where =
        " where p.deleted=0 " + (pid != null ? " and p.id=:pid" : "") +
        (name != null && !name.isBlank() ? " and lower(p.name_en) like :name"
                                         : "") +
        (cid != null ? " and p.category_id=:cid" : "") +
        (active != null ? " and p.active=:active" : "") +
        (min != null ? " and p.stock_quantity>=:min" : "") +
        (max != null ? " and p.stock_quantity<=:max" : "") + stock(st);
    String sql = "select " +
                 "p.id,p.name_en,p.sku,c.id,c.name_en,p.active,p.stock_" +
                 "quantity,p.low_stock_threshold,p.price,p.average_rating,p." +
                 "reviews_count,(select max(h.movement_at) from " +
                 "inventory_history h where h.product_id=p.id) lastm from " +
                 "products p join categories c on c.id=p.category_id" +
                 where;
    String count = "select count(*) from products p join categories c on " +
                   "c.id=p.category_id" +
                   where;
    Query q = em.createNativeQuery(sql + order(pg, "p.id"));
    set(q, pid, name, cid, active, min, max);
    q.setFirstResult((int)pg.getOffset()).setMaxResults(pg.getPageSize());
    Query cq = em.createNativeQuery(count);
    set(cq, pid, name, cid, active, min, max);
    List<CurrentInventoryRow> rows = new ArrayList<>();
    for (Object o : q.getResultList()) {
      Object[] a = (Object[])o;
      int stock = num(a[6]).intValue(), th = num(a[7]).intValue();
      BigDecimal price = bd(a[8]);
      rows.add(new CurrentInventoryRow(
          num(a[0]).longValue(), (String)a[1], (String)a[2],
          num(a[3]).longValue(), (String)a[4], bool(a[5]), stock, th,
          status(stock, th), null, null, price,
          price.multiply(BigDecimal.valueOf(stock)), bd(a[9]),
          num(a[10]).longValue(), inst(a[11])));
    }
    return new PageImpl<>(rows, pg, num(cq.getSingleResult()).longValue());
  }
  private String stock(StockStatus s) {
    if (s == null)
      return "";
    return switch (s) {
      case OUT_OF_STOCK -> " and p.stock_quantity=0";
      case LOW_STOCK ->
        " and p.stock_quantity>0 and p.stock_quantity<=p.low_stock_threshold";
      case IN_STOCK -> " and p.stock_quantity>p.low_stock_threshold";
    };
  }
  private void set(Query q, Long pid, String name, Long cid, Boolean active,
                   Integer min, Integer max) {
    if (pid != null)
      q.setParameter("pid", pid);
    if (name != null && !name.isBlank())
      q.setParameter("name", "%" + name.toLowerCase() + "%");
    if (cid != null)
      q.setParameter("cid", cid);
    if (active != null)
      q.setParameter("active", active);
    if (min != null)
      q.setParameter("min", min);
    if (max != null)
      q.setParameter("max", max);
  }
  public Page<LowStockRow> low(Long cid, Boolean active, Integer threshold,
                               Instant from, Instant to, long days,
                               Pageable pg) {
    int th = threshold == null ? 10 : threshold;
    String w = " where p.deleted=0 and p.stock_quantity>0 and " +
               "p.stock_quantity<=coalesce(:th,p.low_stock_threshold)" +
               (cid != null ? " and p.category_id=:cid" : "") +
               (active != null ? " and p.active=:active" : "");
    String sql =
        "select " +
        "p.id,p.name_en,c.name_en,p.stock_quantity,coalesce(:th,p.low_stock_" +
        "threshold),(select max(h.movement_at) from inventory_history h " +
        "where h.product_id=p.id and " +
        "h.movement_type='ORDER_CREATED'),(select max(h.movement_at) from " +
        "inventory_history h where h.product_id=p.id and " +
        "h.quantity_change>0),(select coalesce(sum(-h.quantity_change),0) " +
        "from inventory_history h where h.product_id=p.id and " +
        "h.movement_type='ORDER_CREATED' and h.movement_at>=:from and " +
        "h.movement_at<:to) sold from products p join categories c on " +
        "c.id=p.category_id" +
        w;
    Query q = em.createNativeQuery(sql + order(pg, "p.stock_quantity"));
    q.setParameter("th", th);
    q.setParameter("from", Timestamp.from(from));
    q.setParameter("to", Timestamp.from(to));
    if (cid != null)
      q.setParameter("cid", cid);
    if (active != null)
      q.setParameter("active", active);
    q.setFirstResult((int)pg.getOffset()).setMaxResults(pg.getPageSize());
    Query cq = em.createNativeQuery("select count(*) from products p join " +
                                    "categories c on c.id=p.category_id" +
                                    w);
    cq.setParameter("th", th);
    if (cid != null)
      cq.setParameter("cid", cid);
    if (active != null)
      cq.setParameter("active", active);
    List<LowStockRow> rows = new ArrayList<>();
    for (Object o : q.getResultList()) {
      Object[] a = (Object[])o;
      int st = num(a[3]).intValue(), et = num(a[4]).intValue();
      long sold = num(a[7]).longValue();
      BigDecimal avg = BigDecimal.valueOf(sold).divide(
          BigDecimal.valueOf(Math.max(days, 1)), 2, RoundingMode.HALF_UP);
      rows.add(new LowStockRow(
          num(a[0]).longValue(), (String)a[1], (String)a[2], st, et,
          Math.max(et - st, 0), inst(a[5]), inst(a[6]), sold, avg,
          avg.signum() == 0
              ? null
              : BigDecimal.valueOf(st).divide(avg, 2, RoundingMode.HALF_UP)));
    }
    return new PageImpl<>(rows, pg, num(cq.getSingleResult()).longValue());
  }
  public Page<MovementRow> movements(String extra, Map<String, Object> params,
                                     Pageable pg) {
    String base = " from inventory_history h join products p on " +
                  "p.id=h.product_id join categories c on c.id=p.category_id " +
                  "left join users u on u.id=h.performed_by_id where 1=1 " +
                  extra;
    String cols = "select h.id,h.product_id," + pn() + "," + cn() +
                  (",h.movement_type,h.quantity_before,h.quantity_change,h." +
                   "quantity_after,h.order_id,h.order_number,h.customer_id,h." +
                   "customer_name_snapshot,u.id,concat(coalesce(u.first_name," +
                   "''),' ',coalesce(u.last_name,'')),h.note,h.movement_at");
    Query q =
        em.createNativeQuery(cols + base + order(pg, "h.movement_at desc"));
    params.forEach(q::setParameter);
    q.setFirstResult((int)pg.getOffset()).setMaxResults(pg.getPageSize());
    Query cq = em.createNativeQuery("select count(*)" + base);
    params.forEach(cq::setParameter);
    List<MovementRow> rows = new ArrayList<>();
    for (Object o : q.getResultList()) {
      Object[] a = (Object[])o;
      int ch = num(a[6]).intValue();
      rows.add(new MovementRow(
          num(a[0]).longValue(), num(a[1]).longValue(), (String)a[2],
          (String)a[3], InventoryMovementType.valueOf((String)a[4]),
          num(a[5]).intValue(), ch, num(a[7]).intValue(),
          ch > 0   ? MovementDirection.INCREASE
          : ch < 0 ? MovementDirection.DECREASE
                   : MovementDirection.ZERO,
          a[8] == null ? null : num(a[8]).longValue(), (String)a[9],
          a[10] == null ? null : num(a[10]).longValue(), (String)a[11],
          a[12] == null ? null : num(a[12]).longValue(), ((String)a[13]).trim(),
          (String)a[14], inst(a[15])));
    }
    return new PageImpl<>(rows, pg, num(cq.getSingleResult()).longValue());
  }
  public List<Object[]> list(String sql, Map<String, Object> p) {
    Query q = em.createNativeQuery(sql);
    p.forEach(q::setParameter);
    return q.getResultList();
  }
  public Object one(String sql, Map<String, Object> p) {
    Query q = em.createNativeQuery(sql);
    p.forEach(q::setParameter);
    return q.getSingleResult();
  }
  public static StockStatus status(int s, int t) {
    return s <= 0 ? StockStatus.OUT_OF_STOCK
    : s <= t      ? StockStatus.LOW_STOCK
                  : StockStatus.IN_STOCK;
  }
  private String order(Pageable p, String d) {
    return " order by " +
        (p.getSort().isSorted()
             ? p.getSort()
                   .stream()
                   .findFirst()
                   .map(o -> o.getProperty() + " " + o.getDirection())
                   .orElse(d)
             : d);
  }
  private Number num(Object o) { return o == null ? 0 : (Number)o; }
  private BigDecimal bd(Object o) {
    return o == null ? BigDecimal.ZERO : (BigDecimal)o;
  }
  private boolean bool(Object o) {
    return o instanceof Boolean b ? b : ((Number)o).intValue() != 0;
  }
  private Instant inst(Object o) {
    return o == null ? null : ((Timestamp)o).toInstant();
  }
}
