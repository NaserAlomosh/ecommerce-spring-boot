package com.smart.ecommerce.dto.address;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

public final class AddressDtos {
  private AddressDtos() {}
  public record CreateAddressRequest(
      @NotBlank @Size(max = 150) String recipientName,
      @NotBlank @Size(max = 20) String phoneNumber,
      @NotBlank @Size(max = 100) String city,
      @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
      @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
      @Size(max = 100) String area, @Size(max = 255) String street,
      @Size(max = 500) String additionalDirections) {}
  public record UpdateAddressRequest(
      @NotBlank @Size(max = 150) String recipientName,
      @NotBlank @Size(max = 20) String phoneNumber,
      @NotBlank @Size(max = 100) String city,
      @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
      @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
      @Size(max = 100) String area, @Size(max = 255) String street,
      @Size(max = 500) String additionalDirections) {}
  public record AddressResponse(Long id, String recipientName,
                                String phoneNumber, String city,
                                BigDecimal latitude, BigDecimal longitude,
                                String area, String street,
                                String additionalDirections,
                                boolean defaultAddress, Instant createdAt,
                                Instant updatedAt) {}
}
