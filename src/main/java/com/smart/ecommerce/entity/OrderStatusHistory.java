package com.smart.ecommerce.entity;

import com.smart.ecommerce.enums.DeliveryFailureReason;
import com.smart.ecommerce.enums.OrderStatus;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "order_status_history",
    indexes =
    {
      @Index(name = "idx_order_status_history_order", columnList = "order_id")
      , @Index(name = "idx_order_status_history_order_changed",
               columnList = "order_id,changed_at")
    })
public class OrderStatusHistory extends BaseEntity {
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;
  @Enumerated(EnumType.STRING)
  @Column(name = "previous_status", length = 30)
  private OrderStatus previousStatus;
  @Enumerated(EnumType.STRING)
  @Column(name = "new_status", nullable = false, length = 30)
  private OrderStatus newStatus;
  @Column(name = "changed_by_user_id", nullable = false)
  private Long changedByUserId;
  @Column(name = "changed_by_role", nullable = false, length = 30)
  private String changedByRole;
  @Column(length = 500) private String note;
  @Enumerated(EnumType.STRING)
  @Column(name = "failure_reason", length = 50)
  private DeliveryFailureReason failureReason;
  @Column(name = "changed_at", nullable = false) private Instant changedAt;
}
