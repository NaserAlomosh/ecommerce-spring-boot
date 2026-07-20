package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.wishlist.WishlistDtos.*;import com.smart.ecommerce.entity.*;import com.smart.ecommerce.exception.ResourceNotFoundException;import com.smart.ecommerce.repository.*;import java.math.*;import lombok.RequiredArgsConstructor;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class WishlistService {
 private final WishlistItemRepository wishlist; private final ProductRepository products; private final CustomerContextService ctx;
 @Transactional(readOnly=true) public WishlistResponse list(){ User u=ctx.currentCustomer(); var rows=wishlist.findByCustomerIdOrderByCreatedAtAsc(u.getId()).stream().map(this::toItem).toList(); return new WishlistResponse(rows, rows.size()); }
 @Transactional public WishlistResponse add(AddWishlistItemRequest r){ User u=ctx.currentCustomer(); Product p=products.findWithImagesById(r.productId()).orElseThrow(()->new ResourceNotFoundException("Product not found")); if(p.isDeleted()) throw new ResourceNotFoundException("Product not found"); if(!wishlist.existsByCustomerIdAndProductId(u.getId(), p.getId())){ WishlistItem wi=new WishlistItem(); wi.setCustomer(u); wi.setProduct(p); wishlist.save(wi);} return list(); }
 @Transactional public void remove(Long productId){ User u=ctx.currentCustomer(); if(!wishlist.existsByCustomerIdAndProductId(u.getId(),productId)) throw new ResourceNotFoundException("Wishlist item not found"); wishlist.deleteByCustomerIdAndProductId(u.getId(), productId); }
 @Transactional(readOnly=true) public WishlistCheckResponse check(Long productId){ User u=ctx.currentCustomer(); return new WishlistCheckResponse(wishlist.existsByCustomerIdAndProductId(u.getId(), productId)); }
 private WishlistItemResponse toItem(WishlistItem wi){ return new WishlistItemResponse(wi.getId(), toProduct(wi.getProduct()), wi.getCreatedAt()); }
 private WishlistProductResponse toProduct(Product p){ BigDecimal eff=p.getDiscountPrice()==null?p.getPrice():p.getDiscountPrice(); BigDecimal pct=p.getDiscountPrice()==null?BigDecimal.ZERO:p.getPrice().subtract(p.getDiscountPrice()).multiply(BigDecimal.valueOf(100)).divide(p.getPrice(),2, RoundingMode.HALF_UP); return new WishlistProductResponse(p.getId(), p.getCategory().getId(), p.getNameEn(), p.getNameAr(), p.getSku(), p.getPrice(), p.getDiscountPrice(), eff, pct, primary(p), p.isActive(), p.getStockQuantity()>0, p.getStockQuantity()); }
 private String primary(Product p){ return p.getImages().stream().filter(ProductImage::isPrimaryImage).findFirst().or(()->p.getImages().stream().findFirst()).map(ProductImage::getImageUrl).orElse(null); }
}
