package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.website.WebsiteDtos.CompanyResponse;
import com.smart.ecommerce.dto.website.WebsiteDtos.CompanyUpdateRequest;
import com.smart.ecommerce.dto.website.WebsiteDtos.ContactResponse;
import com.smart.ecommerce.dto.website.WebsiteDtos.ContactUpdateRequest;
import com.smart.ecommerce.dto.website.WebsiteDtos.SocialSalesLinkResponse;
import com.smart.ecommerce.dto.website.WebsiteDtos.SocialSalesLinkUpdateRequest;
import com.smart.ecommerce.service.WebsiteService;
import com.smart.ecommerce.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/website")
public class AdminWebsiteController {
  private final WebsiteService websiteService;
  private final MessageUtil messages;

  @PutMapping(value = "/company",
              consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<CompanyResponse>
  updateCompany(@Valid @RequestPart("company") CompanyUpdateRequest request,
                @RequestPart(value = "logo", required = false)
                MultipartFile logo,
                @RequestPart(value = "ownerImage", required = false)
                MultipartFile ownerImage) {
    return ApiResponse.success(messages.getMessage("admin.company_updated"),
                               websiteService.updateCompany(request, logo,
                                                            ownerImage));
  }

  @PutMapping("/contact")
  public ApiResponse<ContactResponse>
  updateContact(@Valid @RequestBody ContactUpdateRequest request) {
    return ApiResponse.success(messages.getMessage("admin.contact_updated"),
                               websiteService.updateContact(request));
  }

  @PutMapping("/social-sales-link")
  public ApiResponse<SocialSalesLinkResponse> updateSocialSalesLink(
      @Valid @RequestBody SocialSalesLinkUpdateRequest request) {
    return ApiResponse.success(
        messages.getMessage("admin.social_sales_link_updated"),
        websiteService.updateSocialSalesLink(request));
  }
}
