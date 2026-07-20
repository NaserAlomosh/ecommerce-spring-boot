package com.smart.ecommerce.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

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
    @Column(columnDefinition = "TEXT") private String descriptionEn;
    @Column(columnDefinition = "TEXT") private String descriptionAr;
    @Column(nullable = false, unique = true) private String sku;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal price;
    @Column(nullable = false, length = 3) private String currency = com.smart.ecommerce.enums.CurrencyCode.DEFAULT_CURRENCY;
    @Column(precision = 19, scale = 2) private BigDecimal discountPrice;
    @Column(nullable = false) private int stockQuantity;
    @Column(nullable = false) private int lowStockThreshold;
    @Column(nullable = false) private boolean active;
    @Column(nullable = false) private boolean featured;
    @Column(nullable = false) private boolean deleted;
    @Column(name = "rating_sum", nullable = false) private Long ratingSum = 0L;
    @Column(name = "reviews_count", nullable = false) private Integer reviewsCount = 0;
    @Column(name = "average_rating", nullable = false, precision = 3, scale = 2) private BigDecimal averageRating = BigDecimal.ZERO.setScale(2);
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @BatchSize(size = 50)
    private List<ProductImage> images = new ArrayList<>();
}
