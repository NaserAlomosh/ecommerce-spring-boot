package com.smart.ecommerce.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CurrencyCodeTest {
    @Test void defaultsBlankCurrencyToJod() { assertThat(CurrencyCode.defaultIfBlank("   ")).isEqualTo("JOD"); }
    @Test void normalizesLowercaseAndSpaces() { assertThat(CurrencyCode.defaultIfBlank(" jod ")).isEqualTo("JOD"); }
    @Test void rejectsUnsupportedCurrency() { assertThatThrownBy(() -> CurrencyCode.defaultIfBlank("USD")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Unsupported currency"); }
    @Test void rejectsCurrencySymbols() { assertThatThrownBy(() -> CurrencyCode.defaultIfBlank("د.أ")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Currency symbols"); }
}
