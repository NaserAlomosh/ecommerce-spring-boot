package com.smart.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {
  @Column(nullable = false) private String nameEn;
  @Column(nullable = false) private String nameAr;
  @Column(nullable = false) private boolean active = true;
}
