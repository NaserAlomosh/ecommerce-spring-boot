package com.smart.ecommerce.report.service;
import com.smart.ecommerce.report.dto.ReportDtos.Change;
import com.smart.ecommerce.report.util.GrowthState;
import java.math.*;
public final class ReportMath {
  private ReportMath() {}
  public static final int SCALE = 2;
  public static BigDecimal money(BigDecimal v) {
    return (v == null ? BigDecimal.ZERO : v)
        .setScale(SCALE, RoundingMode.HALF_UP);
  }
  public static BigDecimal divide(BigDecimal a, long b) {
    return b == 0
        ? money(BigDecimal.ZERO)
        : money(a.divide(BigDecimal.valueOf(b), SCALE, RoundingMode.HALF_UP));
  }
  public static Change change(BigDecimal c, BigDecimal p) {
    c = money(c);
    p = money(p);
    BigDecimal amt = money(c.subtract(p));
    int cmp = amt.compareTo(BigDecimal.ZERO);
    if (p.compareTo(BigDecimal.ZERO) == 0) {
      return new Change(
          amt, c.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : null,
          c.compareTo(BigDecimal.ZERO) > 0 ? GrowthState.NEW_ACTIVITY
                                           : GrowthState.NO_PREVIOUS_ACTIVITY);
    }
    return new Change(amt,
                      money(amt.multiply(BigDecimal.valueOf(100))
                                .divide(p, SCALE, RoundingMode.HALF_UP)),
                      cmp > 0   ? GrowthState.INCREASE
                      : cmp < 0 ? GrowthState.DECREASE
                                : GrowthState.NO_CHANGE);
  }
}
