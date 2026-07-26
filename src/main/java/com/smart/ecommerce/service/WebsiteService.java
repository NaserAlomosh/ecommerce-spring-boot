package com.smart.ecommerce.service;

import com.smart.ecommerce.config.WebsiteContentProperties;
import com.smart.ecommerce.dto.website.WebsiteDtos.CompanyResponse;
import com.smart.ecommerce.dto.website.WebsiteDtos.ContactMessageRequest;
import com.smart.ecommerce.dto.website.WebsiteDtos.ContactMessageResponse;
import com.smart.ecommerce.dto.website.WebsiteDtos.ContactResponse;
import com.smart.ecommerce.entity.ContactMessage;
import com.smart.ecommerce.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WebsiteService {
  private final WebsiteContentProperties properties;
  private final ContactMessageRepository contactMessageRepository;

  public CompanyResponse company() {
    var company = properties.company();
    return new CompanyResponse(company.name(), company.descriptionEn(),
                               company.descriptionAr(), company.logoUrl(),
                               company.ownerNameEn(), company.ownerNameAr(),
                               company.ownerImageUrl(), company.locationUrl(),
                               company.locationName(), company.instagramUrl(),
                               company.facebookUrl(), company.tiktokUrl(),
                               company.linkedinUrl());
  }

  public ContactResponse contact() {
    var contact = properties.contact();
    return new ContactResponse(contact.email(), contact.phone(),
                               contact.addressEn(), contact.addressAr());
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
