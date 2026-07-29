package com.smart.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "guest_order_links",
       uniqueConstraints = @UniqueConstraint(name = "uk_guest_order_links_slug",
                                             columnNames = "slug"))
public class GuestOrderLink extends BaseEntity {
  @Column(nullable = false, length = 150) private String title;
  @Column(nullable = false, unique = true, length = 100) private String slug;
  @Column(nullable = false) private boolean active;
}
