package com.smart.ecommerce.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity
@Table(name="order_items", indexes={@Index(name="idx_order_items_order", columnList="order_id"), @Index(name="idx_order_items_product", columnList="product_id")})
public class OrderItem extends BaseEntity {
 @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="order_id", nullable=false) private Order order;
 @Column(name="product_id", nullable=false) private Long productId;
 @Column(name="product_name", nullable=false, length=255) private String productName;
 @Column(name="product_image_url", length=500) private String productImageUrl;
 @Column(nullable=false) private Integer quantity;
 @Column(name="unit_price", nullable=false, precision=19, scale=3) private BigDecimal unitPrice;
 @Column(nullable=false, length=3) private String currency;
 @Column(name="line_total", nullable=false, precision=19, scale=3) private BigDecimal lineTotal;
}
