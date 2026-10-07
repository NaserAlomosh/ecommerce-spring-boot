package com.smart.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smart.ecommerce.enums.Role;
import com.smart.ecommerce.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users",
       uniqueConstraints =
       {
         @UniqueConstraint(name = "uk_users_email", columnNames = "email")
         , @UniqueConstraint(name = "uk_users_phone_number",
                             columnNames = "phone_number")
       },
       indexes =
       {
         @Index(name = "idx_users_role_status", columnList = "role,status")
         , @Index(name = "idx_users_email", columnList = "email")
       })
public class User extends BaseEntity {
  @NotBlank
  @Size(min = 2, max = 80)
  @Column(nullable = false, length = 80)
  private String firstName;
  @NotBlank
  @Size(min = 2, max = 80)
  @Column(nullable = false, length = 80)
  private String lastName;
  @Email @NotBlank @Column(nullable = false, length = 190) private String email;
  @NotBlank
  @Pattern(regexp = "^\\+?[0-9]{8,15}$")
  @Column(nullable = false, length = 20)
  private String phoneNumber;
  @JsonIgnore
  @Column(nullable = false, name = "password_hash")
  private String passwordHash;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private Role role;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private UserStatus status;
  @Column(nullable = false) private boolean emailVerified;
  @Column(nullable = false) private boolean phoneVerified;
  @Column(length = 500) private String profileImage;
  private Instant lastLoginAt;
  @PrePersist
  @PreUpdate
  void normalize() {
    if (email != null)
      email = email.trim().toLowerCase();
  }
}
