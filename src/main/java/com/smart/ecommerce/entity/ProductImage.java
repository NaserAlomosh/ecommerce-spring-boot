package com.smart.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_images")
public class ProductImage extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Column(nullable = false, length = 500) private String imageUrl;
    @Column(nullable = false, length = 500) private String storagePath;
    @Column(nullable = false, length = 50) private String contentType;
    @Column(nullable = false) private long fileSize;
    @Column(nullable = false) private boolean primaryImage;
    @Column(nullable = false) private int sortOrder;
}
