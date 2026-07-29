package com.smart.ecommerce.dto.website;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class WebsiteDtos {
  private WebsiteDtos() {}

  public record CompanyResponse(String name, String descriptionEn,
                                String descriptionAr, String logoUrl,
                                String ownerNameEn, String ownerNameAr,
                                String ownerImageUrl, String locationUrl,
                                String locationName, String instagramUrl,
                                String facebookUrl, String tiktokUrl,
                                String linkedinUrl, boolean active) {}

  public record ContactResponse(String email, String phone, String addressEn,
                                String addressAr, boolean active) {}

  public record SocialSalesLinkResponse(String url) {}

  public record SocialSalesLinkUpdateRequest(
      @NotBlank @Size(max = 1000) String url) {}

  public record CompanyUpdateRequest(
      @NotBlank @Size(max = 200) String name,
      @Size(max = 2000) String descriptionEn,
      @Size(max = 2000) String descriptionAr,
      @Size(max = 200) String ownerNameEn,
      @Size(max = 200) String ownerNameAr,
      @Size(max = 1000) String locationUrl,
      @Size(max = 300) String locationName,
      @Size(max = 1000) String instagramUrl,
      @Size(max = 1000) String facebookUrl,
      @Size(max = 1000) String tiktokUrl,
      @Size(max = 1000) String linkedinUrl,
      @NotNull Boolean active) {}

  public record ContactUpdateRequest(
      @NotBlank @Email @Size(max = 254) String email,
      @NotBlank @Size(max = 30) String phone,
      @Size(max = 500) String addressEn,
      @Size(max = 500) String addressAr,
      @NotNull Boolean active) {}

  public record ContactMessageRequest(
      @NotBlank @Size(max = 120) String name,
      @NotBlank @Email @Size(max = 254) String email,
      @Size(max = 30) String phone,
      @NotBlank @Size(max = 200) String subject,
      @NotBlank @Size(max = 5000) String message) {}

  public record ContactMessageResponse(Long id, Instant submittedAt) {}
}
