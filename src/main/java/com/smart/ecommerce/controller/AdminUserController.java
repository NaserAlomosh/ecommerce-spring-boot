package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.user.UserDtos.AdminCreateUserRequest;
import com.smart.ecommerce.dto.user.UserDtos.StatusRequest;
import com.smart.ecommerce.dto.user.UserDtos.UserResponse;
import com.smart.ecommerce.enums.Role;
import com.smart.ecommerce.enums.UserStatus;
import com.smart.ecommerce.service.UserService;
import com.smart.ecommerce.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final UserService users;
    private final MessageUtil messages;

    @PostMapping
    public ApiResponse<UserResponse> create(@Valid @RequestBody AdminCreateUserRequest request) {
        return success("admin.user_created", users.adminCreate(request));
    }

    @GetMapping
    public ApiResponse<Page<UserResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) UserStatus status,
            Pageable pageable) {
        return success("admin.users", users.search(q, role, status, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> get(@PathVariable Long id) {
        return success("admin.user", users.get(id));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<UserResponse> status(
            @PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return success("admin.status_updated", users.status(id, request));
    }

    private <T> ApiResponse<T> success(String messageKey, T data) {
        return ApiResponse.success(messages.getMessage(messageKey), data);
    }
}
