package com.smart.ecommerce.service;
import java.math.*;
import org.springframework.stereotype.Service;
@Service
public class OrderCalculationService {
  public static final int SCALE = 3;
  public BigDecimal money(BigDecimal v) {
    return v.setScale(SCALE, RoundingMode.HALF_UP);
  }
  public BigDecimal line(BigDecimal unit, int q) {
    return money(unit)
        .multiply(BigDecimal.valueOf(q))
        .setScale(SCALE, RoundingMode.HALF_UP);
  }
  public BigDecimal deliveryFee() { return BigDecimal.ZERO.setScale(SCALE); }
  public BigDecimal discount() { return BigDecimal.ZERO.setScale(SCALE); }
}
