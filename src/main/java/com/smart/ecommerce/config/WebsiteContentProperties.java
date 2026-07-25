package com.smart.ecommerce.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.website")
public record WebsiteContentProperties(Company company, Contact contact) {
  public WebsiteContentProperties {
    company = company == null ? new Company(null, null, null, null) : company;
    contact = contact == null ? new Contact(null, null, null, null) : contact;
  }

  public record Company(String name, String descriptionEn, String descriptionAr,
                        String logoUrl) {}

  public record Contact(String email, String phone, String addressEn,
                        String addressAr) {}
}
