package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.website.WebsiteDtos.CompanyResponse;
import com.smart.ecommerce.dto.website.WebsiteDtos.ContactMessageRequest;
import com.smart.ecommerce.dto.website.WebsiteDtos.ContactMessageResponse;
import com.smart.ecommerce.dto.website.WebsiteDtos.ContactResponse;
import com.smart.ecommerce.service.WebsiteService;
import com.smart.ecommerce.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class WebsiteController {
  private final WebsiteService websiteService;
  private final MessageUtil messages;

  @GetMapping("/company")
  public ApiResponse<CompanyResponse> company() {
    return ApiResponse.success(messages.getMessage("company_details"),
                               websiteService.company());
  }

  @GetMapping("/contact")
  public ApiResponse<ContactResponse> contact() {
    return ApiResponse.success(messages.getMessage("contact_details"),
                               websiteService.contact());
  }

  @PostMapping("/contact/messages")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<ContactMessageResponse>
  submit(@Valid @RequestBody ContactMessageRequest request) {
    return ApiResponse.success(messages.getMessage("contact_message_created"),
                               websiteService.submit(request));
  }
}
