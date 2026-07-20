package com.smart.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Entity
@Table(name="customer_addresses", indexes={@Index(name="idx_customer_addresses_customer", columnList="customer_id"), @Index(name="idx_customer_addresses_customer_active", columnList="customer_id,active"), @Index(name="idx_customer_addresses_customer_default", columnList="customer_id,default_address")})
public class CustomerAddress extends BaseEntity {
 @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="customer_id", nullable=false) private User customer;
 @NotBlank @Size(max=150) @Column(name="recipient_name", nullable=false, length=150) private String recipientName;
 @NotBlank @Size(max=20) @Column(name="phone_number", nullable=false, length=20) private String phoneNumber;
 @NotBlank @Size(max=100) @Column(nullable=false, length=100) private String city;
 @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") @Column(nullable=false, precision=10, scale=7) private BigDecimal latitude;
 @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") @Column(nullable=false, precision=10, scale=7) private BigDecimal longitude;
 @Size(max=100) @Column(length=100) private String area;
 @Size(max=255) @Column(length=255) private String street;
 @Size(max=500) @Column(name="additional_directions", length=500) private String additionalDirections;
 @Column(name="default_address", nullable=false) private boolean defaultAddress=false;
 @Column(nullable=false) private boolean active=true;
}
