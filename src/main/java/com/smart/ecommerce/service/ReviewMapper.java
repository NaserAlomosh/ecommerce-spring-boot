package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.review.ReviewDtos.*;
import com.smart.ecommerce.entity.*;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {
  public ReviewResponse toResponse(Review r) {
    return new ReviewResponse(r.getId(), r.getRating(), r.getComment(),
                              r.getCreatedAt(), r.getUpdatedAt());
  }

  public ProductReviewResponse toProductResponse(Review r) {
    return new ProductReviewResponse(r.getId(), r.getRating(), r.getComment(),
                                     displayName(r.getCustomer()),
                                     r.getCreatedAt(), r.getUpdatedAt());
  }

  public CustomerReviewResponse toCustomerResponse(Review r) {
    return new CustomerReviewResponse(r.getId(), r.getRating(), r.getComment(),
                                      product(r.getProduct(), r.getOrderItem()),
                                      r.getOrder().getOrderNumber(),
                                      r.getOrderItem().getId(),
                                      r.getCreatedAt(), r.getUpdatedAt());
  }

  public AdminReviewResponse toAdminResponse(Review r) {
    User c = r.getCustomer();
    return new AdminReviewResponse(
        r.getId(), r.getRating(), r.getComment(),
        product(r.getProduct(), r.getOrderItem()),
        new CustomerSummary(c.getId(), displayName(c)),
        r.getOrder().getOrderNumber(), r.getOrderItem().getId(),
        r.getCreatedAt(), r.getUpdatedAt());
  }

  private ProductSummary product(Product p, OrderItem i) {
    return new ProductSummary(p.getId(), p.getNameEn(), p.getNameAr(),
                              i.getProductImageUrl(), p.getAverageRating(),
                              p.getReviewsCount());
  }

  private String displayName(User u) {
    return (u.getFirstName() + " " + u.getLastName()).trim();
  }
}
