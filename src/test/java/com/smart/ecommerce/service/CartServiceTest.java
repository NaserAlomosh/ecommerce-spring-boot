package com.smart.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smart.ecommerce.dto.cart.CartDtos.CartResponse;
import com.smart.ecommerce.entity.Cart;
import com.smart.ecommerce.entity.User;
import com.smart.ecommerce.enums.CartStatus;
import com.smart.ecommerce.repository.CartItemRepository;
import com.smart.ecommerce.repository.CartRepository;
import com.smart.ecommerce.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
  @Mock CartRepository carts;
  @Mock CartItemRepository items;
  @Mock ProductRepository products;
  @Mock CustomerContextService ctx;
  CartService service;

  @BeforeEach
  void setUp() {
    service = new CartService(carts, items, products, ctx);
  }

  @Test
  void getCreatesPersistentCartForNewCustomer() {
    User customer = new User();
    customer.setId(29L);
    when(ctx.currentCustomer()).thenReturn(customer);
    when(carts.findByCustomerIdAndStatus(29L, CartStatus.ACTIVE))
        .thenReturn(Optional.empty());
    when(carts.save(any(Cart.class)))
        .thenAnswer(
            invocation -> {
              Cart cart = invocation.getArgument(0);
              cart.setId(7L);
              return cart;
            });

    CartResponse response = service.get();

    assertThat(response.id()).isEqualTo(7L);
    assertThat(response.status()).isEqualTo(CartStatus.ACTIVE);
    assertThat(response.items()).isEmpty();
    assertThat(response.totalItems()).isZero();
    verify(carts).save(any(Cart.class));
  }
}
