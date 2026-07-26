package com.smart.ecommerce.service;

import com.smart.ecommerce.config.WebsiteContentProperties;
import com.smart.ecommerce.dto.website.WebsiteDtos.CompanyResponse;
import com.smart.ecommerce.dto.website.WebsiteDtos.CompanyUpdateRequest;
import com.smart.ecommerce.dto.website.WebsiteDtos.ContactMessageRequest;
import com.smart.ecommerce.dto.website.WebsiteDtos.ContactMessageResponse;
import com.smart.ecommerce.dto.website.WebsiteDtos.ContactResponse;
import com.smart.ecommerce.dto.website.WebsiteDtos.ContactUpdateRequest;
import com.smart.ecommerce.entity.ContactMessage;
import com.smart.ecommerce.entity.WebsiteSettings;
import com.smart.ecommerce.repository.ContactMessageRepository;
import com.smart.ecommerce.repository.WebsiteSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WebsiteService {
  private static final String DEFAULT_SETTINGS_KEY = "default";

  private final WebsiteContentProperties properties;
  private final ContactMessageRepository contactMessageRepository;
  private final WebsiteSettingsRepository websiteSettingsRepository;

  @Transactional(readOnly = true)
  public CompanyResponse company() {
    return websiteSettingsRepository.findBySettingsKey(DEFAULT_SETTINGS_KEY)
        .map(this::companyResponse)
        .orElseGet(this::configuredCompanyResponse);
  }

  @Transactional(readOnly = true)
  public ContactResponse contact() {
    return websiteSettingsRepository.findBySettingsKey(DEFAULT_SETTINGS_KEY)
        .map(this::contactResponse)
        .orElseGet(this::configuredContactResponse);
  }

  @Transactional
  public CompanyResponse updateCompany(CompanyUpdateRequest request) {
    WebsiteSettings settings = settings();
    settings.setCompanyName(request.name().trim());
    settings.setCompanyDescriptionEn(normalizeOptional(request.descriptionEn()));
    settings.setCompanyDescriptionAr(normalizeOptional(request.descriptionAr()));
    settings.setCompanyLogoUrl(normalizeOptional(request.logoUrl()));
    settings.setOwnerNameEn(normalizeOptional(request.ownerNameEn()));
    settings.setOwnerNameAr(normalizeOptional(request.ownerNameAr()));
    settings.setOwnerImageUrl(normalizeOptional(request.ownerImageUrl()));
    settings.setLocationUrl(normalizeOptional(request.locationUrl()));
    settings.setLocationName(normalizeOptional(request.locationName()));
    settings.setInstagramUrl(normalizeOptional(request.instagramUrl()));
    settings.setFacebookUrl(normalizeOptional(request.facebookUrl()));
    settings.setTiktokUrl(normalizeOptional(request.tiktokUrl()));
    settings.setLinkedinUrl(normalizeOptional(request.linkedinUrl()));
    settings.setCompanyActive(request.active());
    return companyResponse(websiteSettingsRepository.save(settings));
  }

  @Transactional
  public ContactResponse updateContact(ContactUpdateRequest request) {
    WebsiteSettings settings = settings();
    settings.setContactEmail(request.email().trim().toLowerCase());
    settings.setContactPhone(request.phone().trim());
    settings.setContactAddressEn(normalizeOptional(request.addressEn()));
    settings.setContactAddressAr(normalizeOptional(request.addressAr()));
    settings.setContactActive(request.active());
    return contactResponse(websiteSettingsRepository.save(settings));
  }

  private CompanyResponse configuredCompanyResponse() {
    var company = properties.company();
    return new CompanyResponse(company.name(), company.descriptionEn(),
                               company.descriptionAr(), company.logoUrl(),
                               company.ownerNameEn(), company.ownerNameAr(),
                               company.ownerImageUrl(), company.locationUrl(),
                               company.locationName(), company.instagramUrl(),
                               company.facebookUrl(), company.tiktokUrl(),
                               company.linkedinUrl(), true);
  }

  private ContactResponse configuredContactResponse() {
    var contact = properties.contact();
    return new ContactResponse(contact.email(), contact.phone(),
                               contact.addressEn(), contact.addressAr(), true);
  }

  private WebsiteSettings settings() {
    return websiteSettingsRepository.findBySettingsKey(DEFAULT_SETTINGS_KEY)
        .orElseGet(this::settingsFromConfiguration);
  }

  private WebsiteSettings settingsFromConfiguration() {
    var company = properties.company();
    var contact = properties.contact();
    WebsiteSettings settings = new WebsiteSettings();
    settings.setCompanyName(company.name());
    settings.setCompanyDescriptionEn(company.descriptionEn());
    settings.setCompanyDescriptionAr(company.descriptionAr());
    settings.setCompanyLogoUrl(company.logoUrl());
    settings.setOwnerNameEn(company.ownerNameEn());
    settings.setOwnerNameAr(company.ownerNameAr());
    settings.setOwnerImageUrl(company.ownerImageUrl());
    settings.setLocationUrl(company.locationUrl());
    settings.setLocationName(company.locationName());
    settings.setInstagramUrl(company.instagramUrl());
    settings.setFacebookUrl(company.facebookUrl());
    settings.setTiktokUrl(company.tiktokUrl());
    settings.setLinkedinUrl(company.linkedinUrl());
    settings.setContactEmail(contact.email());
    settings.setContactPhone(contact.phone());
    settings.setContactAddressEn(contact.addressEn());
    settings.setContactAddressAr(contact.addressAr());
    return settings;
  }

  private CompanyResponse companyResponse(WebsiteSettings settings) {
    return new CompanyResponse(
        settings.getCompanyName(), settings.getCompanyDescriptionEn(),
        settings.getCompanyDescriptionAr(), settings.getCompanyLogoUrl(),
        settings.getOwnerNameEn(), settings.getOwnerNameAr(),
        settings.getOwnerImageUrl(), settings.getLocationUrl(),
        settings.getLocationName(), settings.getInstagramUrl(),
        settings.getFacebookUrl(), settings.getTiktokUrl(),
        settings.getLinkedinUrl(), settings.isCompanyActive());
  }

  private ContactResponse contactResponse(WebsiteSettings settings) {
    return new ContactResponse(
        settings.getContactEmail(), settings.getContactPhone(),
        settings.getContactAddressEn(), settings.getContactAddressAr(),
        settings.isContactActive());
  }

  @Transactional
  public ContactMessageResponse submit(ContactMessageRequest request) {
    ContactMessage message = new ContactMessage();
    message.setName(request.name().trim());
    message.setEmail(request.email().trim().toLowerCase());
    message.setPhone(normalizeOptional(request.phone()));
    message.setSubject(request.subject().trim());
    message.setMessage(request.message().trim());
    ContactMessage saved = contactMessageRepository.save(message);
    return new ContactMessageResponse(saved.getId(), saved.getCreatedAt());
  }

  private String normalizeOptional(String value) {
    return value == null || value.isBlank() ? null : value.trim();
  }
}
