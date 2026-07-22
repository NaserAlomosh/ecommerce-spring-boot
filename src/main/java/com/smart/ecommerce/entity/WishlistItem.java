package com.smart.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wishlist_items",
       uniqueConstraints =
           @UniqueConstraint(name = "uk_wishlist_customer_product",
                             columnNames = {"customer_id", "product_id"}),
       indexes =
       {
         @Index(name = "idx_wishlist_customer_created",
                columnList = "customer_id,created_at")
         ,
             @Index(name = "idx_wishlist_product", columnList = "product_id")
       })
public class WishlistItem extends BaseEntity {
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", nullable = false)
  private User customer;
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;
}
