package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.website.WebsiteDtos.CompanyResponse;
import com.smart.ecommerce.dto.website.WebsiteDtos.CompanyUpdateRequest;
import com.smart.ecommerce.dto.website.WebsiteDtos.ContactResponse;
import com.smart.ecommerce.dto.website.WebsiteDtos.ContactUpdateRequest;
import com.smart.ecommerce.service.WebsiteService;
import com.smart.ecommerce.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/website")
public class AdminWebsiteController {
  private final WebsiteService websiteService;
  private final MessageUtil messages;

  @PutMapping("/company")
  public ApiResponse<CompanyResponse>
  updateCompany(@Valid @RequestBody CompanyUpdateRequest request) {
    return ApiResponse.success(messages.getMessage("admin.company_updated"),
                               websiteService.updateCompany(request));
  }

  @PutMapping("/contact")
  public ApiResponse<ContactResponse>
  updateContact(@Valid @RequestBody ContactUpdateRequest request) {
    return ApiResponse.success(messages.getMessage("admin.contact_updated"),
                               websiteService.updateContact(request));
  }
}
