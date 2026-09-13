package com.smart.ecommerce.report.service;
import com.smart.ecommerce.report.dto.ReportDtos.DateRangeResponse;
import com.smart.ecommerce.report.util.ReportPeriod;
import java.time.*;
import java.time.temporal.*;
import org.springframework.stereotype.Component;
@Component
public class DateRangeResolver {
  private final Clock clock;
  private final BusinessTimeProperties props;
  public DateRangeResolver(Clock clock, BusinessTimeProperties props) {
    this.clock = clock;
    this.props = props;
  }
  public ResolvedDateRange resolve(ReportPeriod p, LocalDate from,
                                   LocalDate to) {
    ReportPeriod period = p == null ? ReportPeriod.TODAY : p;
    ZoneId z = props.zoneId();
    LocalDate today = LocalDate.now(clock.withZone(z));
    LocalDate f, t;
    switch (period) {
    case TODAY -> {
      f = today;
      t = today;
    }
    case YESTERDAY -> {
      f = today.minusDays(1);
      t = f;
    }
    case THIS_WEEK -> {
      f = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
      t = f.plusDays(6);
    }
    case LAST_WEEK -> {
      f = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
              .minusWeeks(1);
      t = f.plusDays(6);
    }
    case THIS_MONTH -> {
      f = today.withDayOfMonth(1);
      t = today.with(TemporalAdjusters.lastDayOfMonth());
    }
    case LAST_MONTH -> {
      YearMonth ym = YearMonth.from(today).minusMonths(1);
      f = ym.atDay(1);
      t = ym.atEndOfMonth();
    }
    case THIS_YEAR -> {
      f = LocalDate.of(today.getYear(), 1, 1);
      t = LocalDate.of(today.getYear(), 12, 31);
    }
    case LAST_YEAR -> {
      f = LocalDate.of(today.getYear() - 1, 1, 1);
      t = LocalDate.of(today.getYear() - 1, 12, 31);
    }
    case CUSTOM -> {
      if (from == null)
        throw new IllegalArgumentException("report.error.date_from_required");
      if (to == null)
        throw new IllegalArgumentException("report.error.date_to_required");
      if (from.isAfter(to))
        throw new IllegalArgumentException("report.error.invalid_date_range");
      f = from;
      t = to;
    }
    default ->
      throw new IllegalArgumentException("report.error.invalid_period");
    }
    return of(f, t, z);
  }
  public ResolvedDateRange previous(ResolvedDateRange r) {
    long days = ChronoUnit.DAYS.between(r.dateFrom(), r.dateTo()) + 1;
    return of(r.dateFrom().minusDays(days), r.dateFrom().minusDays(1),
              r.zoneId());
  }
  private ResolvedDateRange of(LocalDate f, LocalDate t, ZoneId z) {
    return new ResolvedDateRange(f, t, f.atStartOfDay(z).toInstant(),
                                 t.plusDays(1).atStartOfDay(z).toInstant(), z);
  }
  public record ResolvedDateRange(LocalDate dateFrom, LocalDate dateTo,
                                  Instant startInclusive, Instant endExclusive,
                                  ZoneId zoneId) {
    public DateRangeResponse response() {
      return new DateRangeResponse(dateFrom, dateTo, startInclusive,
                                   endExclusive, zoneId.toString());
    }
    public long daysInclusive() {
      return ChronoUnit.DAYS.between(dateFrom, dateTo) + 1;
    }
  }
}
