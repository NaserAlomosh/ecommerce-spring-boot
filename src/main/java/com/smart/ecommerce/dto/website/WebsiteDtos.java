package com.smart.ecommerce.dto.website;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
                                String linkedinUrl) {}

  public record ContactResponse(String email, String phone, String addressEn,
                                String addressAr) {}

  public record ContactMessageRequest(
      @NotBlank @Size(max = 120) String name,
      @NotBlank @Email @Size(max = 254) String email,
      @Size(max = 30) String phone,
      @NotBlank @Size(max = 200) String subject,
      @NotBlank @Size(max = 5000) String message) {}

  public record ContactMessageResponse(Long id, Instant submittedAt) {}
}
