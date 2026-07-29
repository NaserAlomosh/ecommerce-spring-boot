package com.smart.ecommerce.dto.guest;

import com.smart.ecommerce.dto.order.OrderDtos.PublicOrderItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public final class GuestOrderDtos {
  private GuestOrderDtos() {}

  public record GuestOrderRequest(
      @NotBlank @Size(max = 150) String customerName,
      @NotBlank @Pattern(regexp = "^\\+?[0-9]{8,15}$") String phoneNumber,
      @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
      @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
      @Size(max = 500) String address,
      @NotEmpty @Size(max = 100) List<@Valid PublicOrderItemRequest> items) {}
}
