package com.smart.ecommerce.entity;
import com.smart.ecommerce.enums.OtpPurpose;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name = "email_otps", indexes =
                            {
                              @Index(name = "idx_email_otp_user_purpose",
                                     columnList = "user_id,purpose")
                              ,
                                  @Index(name = "idx_email_otp_email",
                                         columnList = "email")
                            })
public class EmailOtp extends BaseEntity {
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
  @Column(nullable = false, length = 190) private String email;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 40)
  private OtpPurpose purpose;
  @Column(nullable = false, length = 128) private String otpHash;
  @Column(nullable = false) private Instant expiresAt;
  private Instant consumedAt;
  @Column(nullable = false) private int attemptCount;
  @Column(nullable = false) private int maxAttempts;
  @Column(nullable = false) private Instant resendAvailableAt;
  public boolean usable() {
    return consumedAt == null && expiresAt.isAfter(Instant.now()) &&
        attemptCount < maxAttempts;
  }
}
