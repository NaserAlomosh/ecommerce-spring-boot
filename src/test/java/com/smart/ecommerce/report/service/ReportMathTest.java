package com.smart.ecommerce.report.service;
import static org.assertj.core.api.Assertions.*;
import com.smart.ecommerce.report.util.GrowthState;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
class ReportMathTest {
  @Test
  void safeBigDecimalDivisionAndChangeStates() {
    assertThat(ReportMath.divide(new BigDecimal("10"), 3))
        .isEqualByComparingTo("3.33");
    assertThat(
        ReportMath.change(BigDecimal.ZERO, BigDecimal.ZERO).growthState())
        .isEqualTo(GrowthState.NO_PREVIOUS_ACTIVITY);
    var c = ReportMath.change(BigDecimal.TEN, BigDecimal.ZERO);
    assertThat(c.percentage()).isNull();
    assertThat(c.growthState()).isEqualTo(GrowthState.NEW_ACTIVITY);
    assertThat(ReportMath.change(new BigDecimal("80"), new BigDecimal("100"))
                   .percentage())
        .isEqualByComparingTo("-20.00");
  }
}
