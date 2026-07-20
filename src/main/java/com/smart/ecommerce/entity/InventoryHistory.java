package com.smart.ecommerce.entity;

import com.smart.ecommerce.enums.InventoryMovementType;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity
@Table(name="inventory_history", indexes={
 @Index(name="idx_inventory_history_product", columnList="product_id"),
 @Index(name="idx_inventory_history_movement_type", columnList="movement_type"),
 @Index(name="idx_inventory_history_created_at", columnList="created_at"),
 @Index(name="idx_inventory_history_order", columnList="order_id")
})
public class InventoryHistory extends BaseEntity {
 @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="product_id", nullable=false) private Product product;
 @Enumerated(EnumType.STRING) @Column(name="movement_type", nullable=false, length=50) private InventoryMovementType movementType;
 @Column(name="quantity_before", nullable=false) private int quantityBefore;
 @Column(name="quantity_change", nullable=false) private int quantityChange;
 @Column(name="quantity_after", nullable=false) private int quantityAfter;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="order_id") private Order order;
 @Column(name="order_number", length=30) private String orderNumber;
 @Column(name="customer_id") private Long customerId;
 @Column(name="customer_name_snapshot", length=180) private String customerNameSnapshot;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="performed_by_id") private User performedBy;
 @Column(length=500) private String note;
 @Column(name="movement_at", nullable=false) private Instant movementAt;
}
