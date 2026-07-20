package com.smart.ecommerce.dto.user;

import com.smart.ecommerce.enums.Role;
import com.smart.ecommerce.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class UserDtos {
    private UserDtos() {}

    public record UserResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            Role role,
            UserStatus status,
            boolean emailVerified,
            boolean phoneVerified,
            String profileImage,
            Instant lastLoginAt,
            Instant createdAt,
            Instant updatedAt) {}

    public record UpdateProfileRequest(
            @NotBlank @Size(min = 2, max = 80) String firstName,
            @NotBlank @Size(min = 2, max = 80) String lastName,
            @NotBlank @Pattern(regexp = "^\\+?[0-9]{8,15}$") String phoneNumber,
            String profileImage) {}

    public record ChangePasswordRequest(
            @NotBlank String currentPassword,
            @NotBlank @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,72}$")
                    String newPassword) {}

    public record AdminCreateUserRequest(
            @NotBlank @Size(min = 2, max = 80) String firstName,
            @NotBlank @Size(min = 2, max = 80) String lastName,
            @Email @NotBlank String email,
            @NotBlank @Pattern(regexp = "^\\+?[0-9]{8,15}$") String phoneNumber,
            @NotBlank @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,72}$") String password,
            @NotNull Role role) {}

    public record StatusRequest(@NotNull UserStatus status) {}

    public record AdminChangePasswordRequest(
            @NotBlank @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,72}$") String newPassword) {}
}
