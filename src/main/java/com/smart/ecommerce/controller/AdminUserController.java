package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.user.UserDtos.*;
import com.smart.ecommerce.enums.Role;
import com.smart.ecommerce.enums.UserStatus;
import com.smart.ecommerce.service.UserService;
import com.smart.ecommerce.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUB_ADMIN')")
public class AdminUserController {
  private final UserService users;
  private final MessageUtil messages;

  @PostMapping
  public ApiResponse<UserResponse>
  create(Authentication auth,
         @Valid @RequestBody AdminCreateUserRequest request) {
    return success("admin.user_created",
                   users.adminCreate(auth.getName(), request));
  }

  @GetMapping
  public ApiResponse<Page<UserResponse>>
  search(@RequestParam(required = false) String q,
         @RequestParam(required = false) Role role,
         @RequestParam(required = false) UserStatus status, Pageable pageable) {
    return success("admin.users", users.search(q, role, status, pageable));
  }

  @GetMapping("/{id}")
  public ApiResponse<UserResponse> get(@PathVariable Long id) {
    return success("admin.user", users.get(id));
  }

  @PatchMapping("/{id}/status")
  public ApiResponse<UserResponse>
  status(Authentication auth, @PathVariable Long id,
         @Valid @RequestBody StatusRequest request) {
    return success("admin.status_updated",
                   users.status(auth.getName(), id, request));
  }

  @PatchMapping("/{id}/password")
  public ApiResponse<Void>
  changePassword(Authentication auth, @PathVariable Long id,
                 @Valid @RequestBody AdminChangePasswordRequest request) {
    users.adminChangePassword(auth.getName(), id, request);
    return success("admin.password_changed", null);
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(Authentication auth, @PathVariable Long id) {
    users.delete(auth.getName(), id);
    return success("admin.user_deleted", null);
  }

  private <T> ApiResponse<T> success(String messageKey, T data) {
    return ApiResponse.success(messages.getMessage(messageKey), data);
  }
}
