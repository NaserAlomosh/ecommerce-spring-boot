package com.smart.ecommerce.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "employee_sales_links",
       uniqueConstraints = @UniqueConstraint(name = "uk_employee_sales_links_token_hash",
                                             columnNames = "token_hash"))
public class EmployeeSalesLink extends BaseEntity {
  @Column(name = "token_hash", nullable = false, length = 64,
          columnDefinition = "char(64)")
  private String tokenHash;
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", nullable = false)
  private User employee;
  @Column(name = "expires_at") private Instant expiresAt;
  @Column(nullable = false) private boolean active = true;
}
