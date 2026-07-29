package com.smart.ecommerce.dto.guest;

import static org.assertj.core.api.Assertions.assertThat;

import com.smart.ecommerce.dto.guest.GuestOrderDtos.*;
import com.smart.ecommerce.dto.order.OrderDtos.PublicOrderItemRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class GuestOrderDtosValidationTest {
  private final Validator validator =
      Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void acceptsMinimumGuestOrderAndRejectsClientFinancialFieldsByDesign() {
    GuestOrderRequest request = new GuestOrderRequest(
        "Guest", "+962790000000", new BigDecimal("31.95"),
        new BigDecimal("35.91"), null,
        List.of(new PublicOrderItemRequest(5L, 2)));

    assertThat(validator.validate(request)).isEmpty();
    assertThat(GuestOrderRequest.class.getRecordComponents())
        .extracting(component -> component.getName())
        .containsExactly("customerName", "phoneNumber", "latitude",
                         "longitude", "address", "items");
  }

  @Test
  void rejectsInvalidSlugAndEmptyItems() {
    GuestLinkRequest link =
        new GuestLinkRequest("Facebook", "Facebook Link", true);
    GuestOrderRequest order = new GuestOrderRequest(
        "", "123", new BigDecimal("91"), new BigDecimal("181"), null,
        List.of());

    assertThat(validator.validate(link)).isNotEmpty();
    assertThat(validator.validate(order)).hasSizeGreaterThanOrEqualTo(5);
  }
}
