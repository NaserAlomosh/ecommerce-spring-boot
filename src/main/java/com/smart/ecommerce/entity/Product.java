package com.smart.ecommerce.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(nullable = false) private String nameEn;
    @Column(nullable = false) private String nameAr;
    @Lob private String descriptionEn;
    @Lob private String descriptionAr;
    @Column(nullable = false, unique = true) private String sku;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal price;
    @Column(precision = 19, scale = 2) private BigDecimal discountPrice;
    @Column(nullable = false) private int stockQuantity;
    @Column(nullable = false) private int lowStockThreshold;
    @Column(nullable = false) private boolean active;
    @Column(nullable = false) private boolean featured;
    @Column(nullable = false) private boolean deleted;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<ProductImage> images = new ArrayList<>();
}
