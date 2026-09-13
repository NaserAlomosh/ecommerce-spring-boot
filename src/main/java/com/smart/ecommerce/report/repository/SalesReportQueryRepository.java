package com.smart.ecommerce.report.repository;
import com.smart.ecommerce.report.util.ReportGranularity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Repository;
@Repository
public class SalesReportQueryRepository {
  private final EntityManager em;
  public SalesReportQueryRepository(EntityManager em) { this.em = em; }
  public List<TrendRow> trend(Instant from, Instant to, ReportGranularity g,
                              ZoneId zone, Long productId) {
    String fmt = switch (g) {
      case HOUR -> "%Y-%m-%d %H:00";
      case DAY -> "%Y-%m-%d";
      case WEEK -> "%x-W%v";
      case MONTH -> "%Y-%m";
      case YEAR -> "%Y";
    };
    String sql =
        "select " +
        "date_format(convert_tz(coalesce(o.completed_at,o.created_at),'+00:" +
        "00',:tz), '" +
        fmt +
        ("') label, min(coalesce(o.completed_at,o.created_at)) periodStart, " +
         "coalesce(sum(oi.line_total),0) revenue, count(distinct o.id) " +
         "ordersCount, coalesce(sum(oi.quantity),0) itemsSold from orders o " +
         "join order_items oi on oi.order_id=o.id where o.status='COMPLETED' " +
         "and coalesce(o.completed_at,o.created_at)>=:from and " +
         "coalesce(o.completed_at,o.created_at)<:to ") +
        (productId != null ? " and oi.product_id=:productId " : "") +
        " group by label order by periodStart";
    Query q = em.createNativeQuery(sql)
                  .setParameter("tz", zone.getRules().getOffset(from).getId())
                  .setParameter("from", Timestamp.from(from))
                  .setParameter("to", Timestamp.from(to));
    if (productId != null)
      q.setParameter("productId", productId);
    List<?> rows = q.getResultList();
    List<TrendRow> out = new ArrayList<>();
    for (Object r : rows) {
      Object[] a = (Object[])r;
      out.add(new TrendRow((String)a[0], ((Timestamp)a[1]).toInstant(),
                           (BigDecimal)a[2], ((Number)a[3]).longValue(),
                           ((Number)a[4]).longValue()));
    }
    return out;
  }
  public record TrendRow(String label, Instant periodStart, BigDecimal revenue,
                         long ordersCount, long itemsSold) {}
}
