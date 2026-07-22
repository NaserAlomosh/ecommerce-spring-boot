package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.auth.AuthDtos.LoginRequest;
import com.smart.ecommerce.dto.auth.AuthDtos.TokenResponse;
import com.smart.ecommerce.service.AuthService;
import com.smart.ecommerce.util.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LegacyAuthController {
  private final AuthService auth;
  private final MessageUtil messages;

  @PostMapping("/api/v1/login")
  public ApiResponse<TokenResponse>
  login(@Valid @RequestBody LoginRequest request,
        HttpServletRequest httpRequest) {
    return ApiResponse.success(messages.getMessage("auth.logged_in"),
                               auth.login(request, ip(httpRequest)));
  }

  private String ip(HttpServletRequest request) {
    return request.getRemoteAddr();
  }
}
