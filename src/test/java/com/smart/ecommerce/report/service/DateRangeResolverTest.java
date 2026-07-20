package com.smart.ecommerce.report.service;
import static org.assertj.core.api.Assertions.*;import com.smart.ecommerce.report.util.ReportPeriod;import java.time.*;import org.junit.jupiter.api.Test;
class DateRangeResolverTest{DateRangeResolver resolver(){return new DateRangeResolver(Clock.fixed(Instant.parse("2026-07-20T10:00:00Z"),ZoneOffset.UTC),new BusinessTimeProperties("Asia/Amman"));}
 @Test void todayUsesBusinessTimezone(){var r=resolver().resolve(ReportPeriod.TODAY,null,null);assertThat(r.dateFrom()).isEqualTo(LocalDate.of(2026,7,20));assertThat(r.startInclusive()).isEqualTo(LocalDate.of(2026,7,20).atStartOfDay(ZoneId.of("Asia/Amman")).toInstant());}
 @Test void yesterdayAndMondayWeekStart(){var y=resolver().resolve(ReportPeriod.YESTERDAY,null,null);assertThat(y.dateFrom()).isEqualTo(LocalDate.of(2026,7,19));var w=resolver().resolve(ReportPeriod.THIS_WEEK,null,null);assertThat(w.dateFrom().getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);}
 @Test void customValidationAndPreviousEqualDuration(){assertThatThrownBy(()->resolver().resolve(ReportPeriod.CUSTOM,null,LocalDate.now())).isInstanceOf(IllegalArgumentException.class);var r=resolver().resolve(ReportPeriod.CUSTOM,LocalDate.of(2026,7,1),LocalDate.of(2026,7,10));var p=resolver().previous(r);assertThat(p.daysInclusive()).isEqualTo(r.daysInclusive());assertThat(p.dateTo()).isEqualTo(LocalDate.of(2026,6,30));}
}
