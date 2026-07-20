package com.smart.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity
@Table(name="cart_items", uniqueConstraints=@UniqueConstraint(name="uk_cart_items_cart_product", columnNames={"cart_id","product_id"}), indexes={@Index(name="idx_cart_items_cart_created", columnList="cart_id,created_at"), @Index(name="idx_cart_items_product", columnList="product_id")})
public class CartItem extends BaseEntity {
 @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="cart_id", nullable=false) private Cart cart;
 @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="product_id", nullable=false) private Product product;
 @Column(nullable=false) private int quantity;
}
