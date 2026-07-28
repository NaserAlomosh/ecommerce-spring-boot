package com.smart.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "website_settings")
public class WebsiteSettings extends BaseEntity {
  @Column(nullable = false, unique = true, length = 40)
  private String settingsKey = "default";

  @Column(length = 200)
  private String companyName;

  @Column(length = 2000)
  private String companyDescriptionEn;

  @Column(length = 2000)
  private String companyDescriptionAr;

  @Column(length = 1000)
  private String companyLogoUrl;

  @Column(length = 1000)
  private String companyLogoStoragePath;

  @Column(length = 200)
  private String ownerNameEn;

  @Column(length = 200)
  private String ownerNameAr;

  @Column(length = 1000)
  private String ownerImageUrl;

  @Column(length = 1000)
  private String ownerImageStoragePath;

  @Column(length = 1000)
  private String locationUrl;

  @Column(length = 300)
  private String locationName;

  @Column(length = 1000)
  private String instagramUrl;

  @Column(length = 1000)
  private String facebookUrl;

  @Column(length = 1000)
  private String tiktokUrl;

  @Column(length = 1000)
  private String linkedinUrl;

  @Column(length = 1000)
  private String socialSalesLink;

  @Column(nullable = false)
  private boolean companyActive = true;

  @Column(length = 254)
  private String contactEmail;

  @Column(length = 30)
  private String contactPhone;

  @Column(length = 500)
  private String contactAddressEn;

  @Column(length = 500)
  private String contactAddressAr;

  @Column(nullable = false)
  private boolean contactActive = true;
}
