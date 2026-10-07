package com.smart.ecommerce.dto.guest;

import static org.assertj.core.api.Assertions.assertThat;

import com.smart.ecommerce.dto.guest.GuestOrderDtos.GuestLinkRequest;
import com.smart.ecommerce.dto.guest.GuestOrderDtos.GuestLinkResponse;
import com.smart.ecommerce.dto.guest.GuestOrderDtos.GuestLinkStatusRequest;
import com.smart.ecommerce.dto.guest.GuestOrderDtos.GuestOrderRequest;
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
  void acceptsOnlyMinimumGuestOrderFields() {
    GuestOrderRequest request = new GuestOrderRequest(
        "Guest", "+962790000000", new BigDecimal("31.95"),
        new BigDecimal("35.91"), "Amman",
        List.of(new PublicOrderItemRequest(5L, 2)));

    assertThat(validator.validate(request)).isEmpty();
    assertThat(GuestOrderRequest.class.getRecordComponents())
        .extracting(component -> component.getName())
        .containsExactly("customerName", "phoneNumber", "latitude",
                         "longitude", "address", "items");
  }

  @Test
  void exposesAndValidatesGuestLinkDtos() {
    GuestLinkRequest valid =
        new GuestLinkRequest("Facebook", "facebook", true);
    GuestLinkRequest invalid =
        new GuestLinkRequest("", "Facebook Link", true);

    assertThat(validator.validate(valid)).isEmpty();
    assertThat(validator.validate(invalid)).hasSize(2);
    assertThat(GuestLinkResponse.class.getRecordComponents())
        .extracting(component -> component.getName())
        .containsExactly("id", "title", "slug", "active", "createdAt",
                         "updatedAt");
    assertThat(GuestLinkStatusRequest.class.getRecordComponents())
        .extracting(component -> component.getName())
        .containsExactly("active");
  }

  @Test
  void rejectsInvalidCustomerAndEmptyItems() {
    GuestOrderRequest order = new GuestOrderRequest(
        "", "123", new BigDecimal("91"), new BigDecimal("181"), null,
        List.of());

    assertThat(validator.validate(order)).hasSizeGreaterThanOrEqualTo(5);
  }
}
