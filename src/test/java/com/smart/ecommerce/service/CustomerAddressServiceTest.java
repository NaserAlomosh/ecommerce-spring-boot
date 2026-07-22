package com.smart.ecommerce.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.smart.ecommerce.dto.address.AddressDtos.*;
import com.smart.ecommerce.entity.*;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.repository.CustomerAddressRepository;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerAddressServiceTest {
  @Mock CustomerAddressRepository addresses;
  @Mock CustomerContextService ctx;
  CustomerAddressMapper mapper = new CustomerAddressMapper();
  CustomerAddressService service;
  @BeforeEach
  void setUp() {
    service = new CustomerAddressService(addresses, ctx, mapper);
  }
  private User user(long id) {
    User u = new User();
    u.setId(id);
    return u;
  }
  private CreateAddressRequest create() {
    return new CreateAddressRequest(
        "Naser Alomosh", "0791234567", "Amman", new BigDecimal("31.9975"),
        new BigDecimal("35.8372"), null, null, null);
  }
  private CustomerAddress address(long id, User u, boolean def) {
    CustomerAddress a = mapper.toEntity(create());
    a.setId(id);
    a.setCustomer(u);
    a.setActive(true);
    a.setDefaultAddress(def);
    return a;
  }
  @Test
  void firstAddressBecomesDefault() {
    User u = user(1);
    when(ctx.currentCustomer()).thenReturn(u);
    when(addresses.countByCustomerIdAndActiveTrue(1L)).thenReturn(0L);
    when(addresses.save(any())).thenAnswer(i -> i.getArgument(0));
    assertThat(service.create(create()).defaultAddress()).isTrue();
    verify(addresses).lockActiveByCustomerId(1L);
  }
  @Test
  void secondAddressIsNotDefault() {
    User u = user(1);
    when(ctx.currentCustomer()).thenReturn(u);
    when(addresses.countByCustomerIdAndActiveTrue(1L)).thenReturn(1L);
    when(addresses.save(any())).thenAnswer(i -> i.getArgument(0));
    assertThat(service.create(create()).defaultAddress()).isFalse();
  }
  @Test
  void updateAddress() {
    User u = user(1);
    CustomerAddress a = address(10, u, false);
    when(ctx.currentCustomer()).thenReturn(u);
    when(addresses.findByIdAndCustomerIdAndActiveTrue(10L, 1L))
        .thenReturn(Optional.of(a));
    UpdateAddressRequest r = new UpdateAddressRequest(
        "Sara", "078", "Irbid", BigDecimal.ONE, BigDecimal.TEN, "A", "S", "D");
    AddressResponse out = service.update(10L, r);
    assertThat(out.recipientName()).isEqualTo("Sara");
    assertThat(out.defaultAddress()).isFalse();
  }
  @Test
  void deleteDefaultAssignsAnotherDefault() {
    User u = user(1);
    CustomerAddress a = address(1, u, true);
    CustomerAddress b = address(2, u, false);
    when(ctx.currentCustomer()).thenReturn(u);
    when(addresses.findByIdAndCustomerIdAndActiveTrue(1L, 1L))
        .thenReturn(Optional.of(a));
    when(addresses.findFirstByCustomerIdAndActiveTrueOrderByCreatedAtDesc(1L))
        .thenReturn(Optional.of(b));
    service.delete(1L);
    assertThat(a.isActive()).isFalse();
    assertThat(a.isDefaultAddress()).isFalse();
    assertThat(b.isDefaultAddress()).isTrue();
  }
  @Test
  void setDefaultClearsPreviousDefaults() {
    User u = user(1);
    CustomerAddress a = address(2, u, false);
    when(ctx.currentCustomer()).thenReturn(u);
    when(addresses.findByIdAndCustomerIdAndActiveTrue(2L, 1L))
        .thenReturn(Optional.of(a));
    assertThat(service.setDefault(2L).defaultAddress()).isTrue();
    verify(addresses).clearDefaults(1L);
  }
  @Test
  void rejectsAnotherCustomersAddress() {
    User u = user(1);
    when(ctx.currentCustomer()).thenReturn(u);
    when(addresses.findByIdAndCustomerIdAndActiveTrue(99L, 1L))
        .thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.get(99L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Address not found");
  }
}
