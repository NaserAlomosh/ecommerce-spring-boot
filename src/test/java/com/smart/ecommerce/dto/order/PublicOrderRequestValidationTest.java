package com.smart.ecommerce.dto.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.smart.ecommerce.dto.order.OrderDtos.PublicOrderItemRequest;
import com.smart.ecommerce.dto.order.OrderDtos.PublicOrderLocationRequest;
import com.smart.ecommerce.dto.order.OrderDtos.PublicOrderRequest;
import jakarta.validation.Validation;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PublicOrderRequestValidationTest {
  @Test
  void rejectsAnEmptyProductList() {
    try (var factory = Validation.buildDefaultValidatorFactory()) {
      var request = new PublicOrderRequest(
          "Customer", "+962790000000", validLocation(), List.of(), null);

      assertThat(factory.getValidator().validate(request))
          .anyMatch(v -> v.getPropertyPath().toString().equals("products"));
    }
  }

  @Test
  void validatesNestedLocationAndProductValues() {
    try (var factory = Validation.buildDefaultValidatorFactory()) {
      var location = new PublicOrderLocationRequest(
          "Amman", new BigDecimal("91"), new BigDecimal("181"), null, null,
          null);
      var request = new PublicOrderRequest(
          "Customer", "+962790000000", location,
          List.of(new PublicOrderItemRequest(1L, 0)), null);

      assertThat(factory.getValidator().validate(request))
          .extracting(v -> v.getPropertyPath().toString())
          .contains("location.latitude", "location.longitude",
                    "products[0].quantity");
    }
  }

  private PublicOrderLocationRequest validLocation() {
    return new PublicOrderLocationRequest(
        "Amman", new BigDecimal("31.9539"), new BigDecimal("35.9106"), null,
        null, null);
  }
}
