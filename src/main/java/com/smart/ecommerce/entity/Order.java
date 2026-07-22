package com.smart.ecommerce.entity;

import com.smart.ecommerce.enums.DeliveryFailureReason;
import com.smart.ecommerce.enums.OrderStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

@Getter
@Setter
@Entity
@Table(name = "orders",
       uniqueConstraints = @UniqueConstraint(name = "uk_orders_order_number",
                                             columnNames = "order_number"),
       indexes =
       {
         @Index(name = "idx_orders_customer", columnList = "customer_id")
         ,
             @Index(name = "idx_orders_customer_created",
                    columnList = "customer_id,created_at"),
             @Index(name = "idx_orders_customer_status",
                    columnList = "customer_id,status"),
             @Index(name = "idx_orders_status", columnList = "status"),
             @Index(name = "idx_orders_created", columnList = "created_at")
       })
public class Order extends BaseEntity {
  @Column(name = "order_number", nullable = false, unique = true, length = 30)
  private String orderNumber;
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", nullable = false)
  private User customer;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assigned_delivery_user_id")
  private User assignedDeliveryUser;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private OrderStatus status;
  @Column(nullable = false, length = 3) private String currency;
  @Column(nullable = false, precision = 19, scale = 3)
  private BigDecimal subtotal;
  @Column(name = "delivery_fee", nullable = false, precision = 19, scale = 3)
  private BigDecimal deliveryFee;
  @Column(name = "discount_amount", nullable = false, precision = 19, scale = 3)
  private BigDecimal discountAmount;
  @Column(name = "total_amount", nullable = false, precision = 19, scale = 3)
  private BigDecimal totalAmount;
  @Column(name = "total_items", nullable = false) private Integer totalItems;
  @Column(name = "customer_note", length = 1000) private String customerNote;
  @Enumerated(EnumType.STRING)
  @Column(name = "failure_reason", length = 50)
  private DeliveryFailureReason failureReason;
  @Column(name = "failure_note", length = 500) private String failureNote;
  @Column(name = "cancellation_reason", length = 500)
  private String cancellationReason;
  @Column(name = "cancelled_at") private Instant cancelledAt;
  @Column(name = "completed_at") private Instant completedAt;
  @Column(name = "recipient_name", nullable = false, length = 150)
  private String recipientName;
  @Column(name = "phone_number", nullable = false, length = 20)
  private String phoneNumber;
  @Column(nullable = false, length = 100) private String city;
  @Column(nullable = false, precision = 10, scale = 7)
  private BigDecimal latitude;
  @Column(nullable = false, precision = 10, scale = 7)
  private BigDecimal longitude;
  @Column(length = 100) private String area;
  @Column(length = 255) private String street;
  @Column(name = "additional_directions", length = 500)
  private String additionalDirections;
  @Version @Column(nullable = false) private Long version;
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
             orphanRemoval = true)
  @OrderBy("createdAt ASC")
  @BatchSize(size = 50)
  private List<OrderItem> items = new ArrayList<>();
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
             orphanRemoval = true)
  @OrderBy("createdAt ASC")
  @BatchSize(size = 50)
  private List<OrderStatusHistory> statusHistory = new ArrayList<>();
}
