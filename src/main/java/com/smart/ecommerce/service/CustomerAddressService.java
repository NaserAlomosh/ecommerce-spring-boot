package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.address.AddressDtos.*;
import com.smart.ecommerce.entity.*;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.repository.CustomerAddressRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerAddressService {
  private final CustomerAddressRepository addresses;
  private final CustomerContextService ctx;
  private final CustomerAddressMapper mapper;
  @Transactional(readOnly = true)
  public List<AddressResponse> list() {
    User u = ctx.currentCustomer();
    return addresses
        .findByCustomerIdAndActiveTrueOrderByDefaultAddressDescCreatedAtDesc(
            u.getId())
        .stream()
        .map(mapper::toResponse)
        .toList();
  }
  @Transactional(readOnly = true)
  public AddressResponse get(Long id) {
    return mapper.toResponse(owned(id));
  }
  @Transactional
  public AddressResponse create(CreateAddressRequest r) {
    User u = ctx.currentCustomer();
    addresses.lockActiveByCustomerId(u.getId());
    CustomerAddress a = mapper.toEntity(r);
    a.setCustomer(u);
    a.setActive(true);
    a.setDefaultAddress(addresses.countByCustomerIdAndActiveTrue(u.getId()) ==
                        0);
    return mapper.toResponse(addresses.save(a));
  }
  @Transactional
  public AddressResponse update(Long id, UpdateAddressRequest r) {
    CustomerAddress a = owned(id);
    mapper.update(a, r);
    return mapper.toResponse(a);
  }
  @Transactional
  public AddressResponse setDefault(Long id) {
    User u = ctx.currentCustomer();
    addresses.lockActiveByCustomerId(u.getId());
    CustomerAddress a =
        addresses.findByIdAndCustomerIdAndActiveTrue(id, u.getId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Address not found"));
    addresses.clearDefaults(u.getId());
    a.setDefaultAddress(true);
    return mapper.toResponse(a);
  }
  @Transactional
  public void delete(Long id) {
    User u = ctx.currentCustomer();
    addresses.lockActiveByCustomerId(u.getId());
    CustomerAddress a =
        addresses.findByIdAndCustomerIdAndActiveTrue(id, u.getId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Address not found"));
    boolean wasDefault = a.isDefaultAddress();
    a.setActive(false);
    a.setDefaultAddress(false);
    if (wasDefault)
      addresses
          .findFirstByCustomerIdAndActiveTrueOrderByCreatedAtDesc(u.getId())
          .ifPresent(n -> n.setDefaultAddress(true));
  }
  private CustomerAddress owned(Long id) {
    User u = ctx.currentCustomer();
    return addresses.findByIdAndCustomerIdAndActiveTrue(id, u.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
  }
}
