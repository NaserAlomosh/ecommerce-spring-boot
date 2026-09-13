package com.smart.ecommerce.entity;

import com.smart.ecommerce.enums.CartStatus;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "carts",
       indexes =
       {
         @Index(name = "idx_carts_customer_status",
                columnList = "customer_id,status")
       },
       uniqueConstraints =
           @UniqueConstraint(name = "uk_carts_customer_status",
                             columnNames = {"customer_id", "status"}))
public class Cart extends BaseEntity {
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", nullable = false)
  private User customer;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private CartStatus status;
  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("createdAt ASC")
  private List<CartItem> items = new ArrayList<>();
}
