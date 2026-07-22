package com.smart.ecommerce.enums;

import java.util.Locale;
import java.util.Set;

public enum CurrencyCode {
  JOD;

  public static final String DEFAULT_CURRENCY = "JOD";
  private static final Set<String> SUPPORTED_CODES = Set.of(DEFAULT_CURRENCY);

  public static String defaultIfBlank(String currency) {
    String normalized = normalizeNullable(currency);
    if (normalized == null)
      return DEFAULT_CURRENCY;
    validateSupported(normalized);
    return normalized;
  }

  public static String normalizeNullable(String currency) {
    if (currency == null || currency.isBlank())
      return null;
    return currency.trim().toUpperCase(Locale.ROOT);
  }

  public static void validateSupported(String currency) {
    if (currency == null || currency.length() != 3) {
      throw new IllegalArgumentException(
          "Currency must be a 3-letter ISO 4217 code");
    }
    if (!currency.matches("[A-Z]{3}")) {
      throw new IllegalArgumentException(
          "Currency symbols are not accepted; use a 3-letter ISO 4217 code");
    }
    if (!SUPPORTED_CODES.contains(currency)) {
      throw new IllegalArgumentException("Unsupported currency: " + currency);
    }
  }
}
