package com.smart.ecommerce.entity;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name = "password_reset_tokens",
       indexes = @Index(name = "idx_pwd_reset_hash", columnList = "token_hash"))
public class PasswordResetToken extends BaseEntity {
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
  @Column(nullable = false, length = 128) private String tokenHash;
  @Column(nullable = false) private Instant expiresAt;
  private Instant consumedAt;
  public boolean usable() {
    return consumedAt == null && expiresAt.isAfter(Instant.now());
  }
}
