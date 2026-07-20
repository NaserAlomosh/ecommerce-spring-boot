package com.smart.ecommerce.controller;
import com.smart.ecommerce.dto.ApiResponse;import com.smart.ecommerce.dto.auth.AuthDtos.*;import com.smart.ecommerce.service.AuthService;import io.swagger.v3.oas.annotations.Operation;import jakarta.servlet.http.HttpServletRequest;import jakarta.validation.Valid;import lombok.RequiredArgsConstructor;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/auth") @RequiredArgsConstructor public class AuthController{private final AuthService auth; private String ip(HttpServletRequest r){return r.getRemoteAddr();}
@PostMapping("/register") @Operation(summary="Register public customer") public ApiResponse<?> register(@Valid @RequestBody RegisterRequest r){return ApiResponse.success("registered",auth.register(r));}
@PostMapping("/login") public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest r,HttpServletRequest req){return ApiResponse.success("logged in",auth.login(r,ip(req)));}
@PostMapping("/refresh") public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshRequest r,HttpServletRequest req){return ApiResponse.success("refreshed",auth.refresh(r,ip(req)));}
@PostMapping("/logout") public ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest r,HttpServletRequest req){auth.logout(r,ip(req));return ApiResponse.success("logged out",null);}
@PostMapping("/verify-email") public ApiResponse<Void> verifyEmail(@Valid @RequestBody EmailOtpRequest r){auth.verifyEmail(r);return ApiResponse.success("email verified",null);}
@PostMapping("/resend-email-otp") public ApiResponse<Void> resend(@Valid @RequestBody EmailRequest r){auth.resendEmailOtp(r);return ApiResponse.success("otp sent",null);}
@PostMapping("/forgot-password") public ApiResponse<Void> forgot(@Valid @RequestBody EmailRequest r){auth.forgotPassword(r);return ApiResponse.success("if the email exists an OTP was sent",null);}
@PostMapping("/verify-password-reset-otp") public ApiResponse<ResetOtpResponse> verifyReset(@Valid @RequestBody EmailOtpRequest r){return ApiResponse.success("otp verified",auth.verifyPasswordResetOtp(r));}
@PostMapping("/reset-password") public ApiResponse<Void> reset(@Valid @RequestBody ResetPasswordRequest r){auth.resetPassword(r);return ApiResponse.success("password reset",null);}
@PostMapping("/social-login") public ApiResponse<TokenResponse> social(@Valid @RequestBody SocialLoginRequest r,HttpServletRequest req){return ApiResponse.success("logged in",auth.social(r,ip(req)));}}
