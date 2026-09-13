package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.auth.AuthDtos.EmailOtpRequest;
import com.smart.ecommerce.dto.auth.AuthDtos.EmailRequest;
import com.smart.ecommerce.dto.auth.AuthDtos.LoginRequest;
import com.smart.ecommerce.dto.auth.AuthDtos.LogoutRequest;
import com.smart.ecommerce.dto.auth.AuthDtos.RefreshRequest;
import com.smart.ecommerce.dto.auth.AuthDtos.RegisterRequest;
import com.smart.ecommerce.dto.auth.AuthDtos.ResetOtpResponse;
import com.smart.ecommerce.dto.auth.AuthDtos.ResetPasswordRequest;
import com.smart.ecommerce.dto.auth.AuthDtos.SocialLoginRequest;
import com.smart.ecommerce.dto.auth.AuthDtos.TokenResponse;
import com.smart.ecommerce.dto.user.UserDtos.UserResponse;
import com.smart.ecommerce.service.AuthService;
import com.smart.ecommerce.util.MessageUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService auth;
  private final MessageUtil messages;

  @PostMapping("/register")
  @Operation(summary = "Register public customer")
  public ApiResponse<UserResponse> register(
      @Valid @RequestBody RegisterRequest request) {
    return success("auth.registered", auth.register(request));
  }

  @PostMapping("/login")
  public ApiResponse<TokenResponse>
  login(@Valid @RequestBody LoginRequest request,
        HttpServletRequest httpRequest) {
    return success("auth.logged_in", auth.login(request, ip(httpRequest)));
  }

  @PostMapping("/refresh")
  public ApiResponse<TokenResponse>
  refresh(@Valid @RequestBody RefreshRequest request,
          HttpServletRequest httpRequest) {
    return success("auth.refreshed", auth.refresh(request, ip(httpRequest)));
  }

  @PostMapping("/logout")
  public ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest request,
                                  HttpServletRequest httpRequest) {
    auth.logout(request, ip(httpRequest));
    return success("auth.logged_out", null);
  }

  @PostMapping("/verify-email")
  public ApiResponse<Void>
  verifyEmail(@Valid @RequestBody EmailOtpRequest request) {
    auth.verifyEmail(request);
    return success("auth.email_verified", null);
  }

  @PostMapping("/resend-email-otp")
  public ApiResponse<Void> resend(@Valid @RequestBody EmailRequest request) {
    auth.resendEmailOtp(request);
    return success("auth.otp_sent", null);
  }

  @PostMapping("/forgot-password")
  public ApiResponse<Void> forgot(@Valid @RequestBody EmailRequest request) {
    auth.forgotPassword(request);
    return success("auth.forgot_password_sent", null);
  }

  @PostMapping("/verify-password-reset-otp")
  public ApiResponse<ResetOtpResponse>
  verifyReset(@Valid @RequestBody EmailOtpRequest request) {
    return success("auth.otp_verified", auth.verifyPasswordResetOtp(request));
  }

  @PostMapping("/reset-password")
  public ApiResponse<Void>
  reset(@Valid @RequestBody ResetPasswordRequest request) {
    auth.resetPassword(request);
    return success("auth.password_reset", null);
  }

  @PostMapping("/social-login")
  public ApiResponse<TokenResponse>
  social(@Valid @RequestBody SocialLoginRequest request,
         HttpServletRequest httpRequest) {
    return success("auth.logged_in", auth.social(request, ip(httpRequest)));
  }

  private String ip(HttpServletRequest request) {
    return request.getRemoteAddr();
  }

  private <T> ApiResponse<T> success(String messageKey, T data) {
    return ApiResponse.success(messages.getMessage(messageKey), data);
  }
}
