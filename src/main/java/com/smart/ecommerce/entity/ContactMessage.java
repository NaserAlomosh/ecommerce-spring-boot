package com.smart.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "contact_messages")
@Getter
@Setter
public class ContactMessage extends BaseEntity {
  @Column(nullable = false, length = 120)
  private String name;

  @Column(nullable = false, length = 254)
  private String email;

  @Column(length = 30)
  private String phone;

  @Column(nullable = false, length = 200)
  private String subject;

  @Column(nullable = false, length = 5000)
  private String message;

  @Column(name = "is_read", nullable = false)
  private boolean read;

  @Column(name = "read_at")
  private Instant readAt;
}
