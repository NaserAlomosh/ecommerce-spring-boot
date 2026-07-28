package com.smart.ecommerce.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class GuestSalesDtos {
  private GuestSalesDtos() {}
  public record CreateSalesLinkRequest(@Future Instant expiresAt) {}
  public record SalesLinkResponse(Long id, String token, String publicPath,
                                  Instant expiresAt, boolean active) {}
  public record GuestOrderItemRequest(@NotNull @Positive Long productId,
                                      @NotNull @Min(1) @Max(999) Integer quantity) {}
  public record GuestOrderRequest(
      @NotBlank @Size(max = 150) String name,
      @NotBlank @Pattern(regexp = "^\\+?[0-9]{8,15}$") String phoneNumber,
      @NotBlank @Size(max = 100) String city,
      @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
      @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
      @Size(max = 100) String area, @Size(max = 255) String street,
      @Size(max = 500) String additionalDirections,
      @Size(max = 1000) String customerNote,
      @NotEmpty @Size(max = 100) List<@Valid GuestOrderItemRequest> items) {}
}
