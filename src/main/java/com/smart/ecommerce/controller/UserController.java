package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.user.UserDtos.ChangePasswordRequest;
import com.smart.ecommerce.dto.user.UserDtos.UpdateProfileRequest;
import com.smart.ecommerce.dto.user.UserDtos.UserResponse;
import com.smart.ecommerce.service.UserService;
import com.smart.ecommerce.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService users;
  private final MessageUtil messages;

  @GetMapping("/me")
  public ApiResponse<UserResponse> me(Authentication authentication) {
    return success("user.profile", users.me(authentication.getName()));
  }

  @PutMapping("/me")
  public ApiResponse<UserResponse>
  update(Authentication authentication,
         @Valid @RequestBody UpdateProfileRequest request) {
    return success("user.profile_updated",
                   users.updateMe(authentication.getName(), request));
  }

  @PutMapping("/me/password")
  public ApiResponse<Void>
  password(Authentication authentication,
           @Valid @RequestBody ChangePasswordRequest request) {
    users.changePassword(authentication.getName(), request);
    return success("user.password_changed", null);
  }

  private <T> ApiResponse<T> success(String messageKey, T data) {
    return ApiResponse.success(messages.getMessage(messageKey), data);
  }
}
