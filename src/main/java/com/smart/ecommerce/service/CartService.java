package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.cart.CartDtos.*;
import com.smart.ecommerce.entity.*;
import com.smart.ecommerce.enums.CartStatus;
import com.smart.ecommerce.enums.CurrencyCode;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.repository.*;
import java.math.*;import java.util.*;
import lombok.RequiredArgsConstructor;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class CartService {
 private final CartRepository carts; private final CartItemRepository items; private final ProductRepository products; private final CustomerContextService ctx;
 @Transactional(readOnly=true) public CartResponse get(){ User u=ctx.currentCustomer(); return toResponse(carts.findByCustomerIdAndStatus(u.getId(), CartStatus.ACTIVE).orElse(empty(u))); }
 @Transactional public CartResponse add(AddCartItemRequest r){ User u=ctx.currentCustomer(); Product p=products.lockWithImagesById(r.productId()).orElseThrow(()->new ResourceNotFoundException("Product not found")); validateAvailable(p); Cart c=carts.lockByCustomerIdAndStatus(u.getId(),CartStatus.ACTIVE).orElseGet(()->{Cart n=new Cart();n.setCustomer(u);n.setStatus(CartStatus.ACTIVE);return carts.save(n);}); CartItem it=items.findByCartIdAndProductId(c.getId(),p.getId()).orElse(null); int q=r.quantity()+(it==null?0:it.getQuantity()); validateQty(q,p); if(it==null){it=new CartItem();it.setCart(c);it.setProduct(p);c.getItems().add(it);} it.setQuantity(q); items.save(it); return get(); }
 @Transactional public CartResponse update(Long id, UpdateCartItemQuantityRequest r){ CartItem it=owned(id); Product p=products.lockWithImagesById(it.getProduct().getId()).orElseThrow(()->new ResourceNotFoundException("Product not found")); validateAvailable(p); validateQty(r.quantity(),p); it.setQuantity(r.quantity()); return get(); }
 @Transactional public void remove(Long id){ items.delete(owned(id)); }
 @Transactional public void clear(){ User u=ctx.currentCustomer(); carts.lockByCustomerIdAndStatus(u.getId(),CartStatus.ACTIVE).ifPresent(c->items.deleteByCartId(c.getId())); }
 private CartItem owned(Long id){ User u=ctx.currentCustomer(); CartItem it=items.findById(id).orElseThrow(()->new ResourceNotFoundException("Cart item not found")); if(!it.getCart().getCustomer().getId().equals(u.getId())) throw new ResourceNotFoundException("Cart item not found"); return it; }
 private Cart empty(User u){ Cart c=new Cart(); c.setCustomer(u); c.setStatus(CartStatus.ACTIVE); return c; }
 private void validateAvailable(Product p){ if(p.isDeleted()) throw new ResourceNotFoundException("Product not found"); if(!p.isActive()) throw new IllegalArgumentException("Product is inactive"); if(p.getStockQuantity()<=0) throw new IllegalArgumentException("Product is unavailable"); }
 private void validateQty(int q, Product p){ if(q<1) throw new IllegalArgumentException("Quantity must be at least 1"); if(q>p.getStockQuantity()) throw new IllegalArgumentException("Insufficient stock"); }
 private CartResponse toResponse(Cart c){ List<CartItemResponse> rs=c.getItems().stream().sorted(Comparator.comparing(CartItem::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))).map(this::toItem).toList(); String currency=cartCurrency(rs); BigDecimal sub=rs.stream().map(CartItemResponse::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add); int total=rs.stream().mapToInt(CartItemResponse::quantity).sum(); return new CartResponse(c.getId(), c.getStatus(), rs, total, rs.size(), sub, currency); }
 private CartItemResponse toItem(CartItem it){ Product p=it.getProduct(); BigDecimal eff=p.getDiscountPrice()==null?p.getPrice():p.getDiscountPrice(); BigDecimal line=eff.multiply(BigDecimal.valueOf(it.getQuantity())); boolean inStock=p.getStockQuantity()>0; boolean avail=!p.isDeleted()&&p.isActive()&&inStock; return new CartItemResponse(it.getId(), new CartProductResponse(p.getId(),p.getNameEn(),p.getNameAr(),p.getSku(),primary(p),p.isActive(),inStock,p.getStockQuantity()), it.getQuantity(), p.getPrice(), p.getCurrency(), p.getDiscountPrice(), eff, line, avail, it.getQuantity()>p.getStockQuantity(), it.getCreatedAt(), it.getUpdatedAt()); }
 private String cartCurrency(List<CartItemResponse> rs){ String currency=CurrencyCode.DEFAULT_CURRENCY; for(CartItemResponse r:rs){ if(!currency.equals(r.currency())) throw new IllegalArgumentException("Mixed currencies in cart are not supported"); } return currency; }
 private String primary(Product p){ return p.getImages().stream().filter(ProductImage::isPrimaryImage).findFirst().or(()->p.getImages().stream().findFirst()).map(ProductImage::getImageUrl).orElse(null); }
}
