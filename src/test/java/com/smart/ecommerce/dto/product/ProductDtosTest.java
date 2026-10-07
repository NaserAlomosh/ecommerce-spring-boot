package com.smart.ecommerce.dto.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.smart.ecommerce.dto.product.ProductDtos.ProductCreateRequest;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductDtosTest {
  @Test
  void createRequestNormalizesCurrency() {
    ProductCreateRequest request = new ProductCreateRequest(
        1L, "Name", "اسم", null, null, "SKU-1", BigDecimal.TEN, " jod ", null,
        1, 0, true, false);
    assertThat(request.currency()).isEqualTo("JOD");
  }

  @Test
  void createRequestKeepsMissingCurrencyNullForDefaulting() {
    ProductCreateRequest request =
        new ProductCreateRequest(1L, "Name", "اسم", null, null, "SKU-1",
                                 BigDecimal.TEN, " ", null, 1, 0, true, false);
    assertThat(request.currency()).isNull();
  }
}
