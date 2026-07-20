package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.address.AddressDtos.*;
import com.smart.ecommerce.entity.CustomerAddress;
import org.springframework.stereotype.Component;

@Component
public class CustomerAddressMapper {
 public CustomerAddress toEntity(CreateAddressRequest r){ CustomerAddress a=new CustomerAddress(); apply(a, r.recipientName(), r.phoneNumber(), r.city(), r.latitude(), r.longitude(), r.area(), r.street(), r.additionalDirections()); return a; }
 public void update(CustomerAddress a, UpdateAddressRequest r){ apply(a, r.recipientName(), r.phoneNumber(), r.city(), r.latitude(), r.longitude(), r.area(), r.street(), r.additionalDirections()); }
 public AddressResponse toResponse(CustomerAddress a){ return new AddressResponse(a.getId(), a.getRecipientName(), a.getPhoneNumber(), a.getCity(), a.getLatitude(), a.getLongitude(), a.getArea(), a.getStreet(), a.getAdditionalDirections(), a.isDefaultAddress(), a.getCreatedAt(), a.getUpdatedAt()); }
 private void apply(CustomerAddress a,String recipientName,String phoneNumber,String city,java.math.BigDecimal latitude,java.math.BigDecimal longitude,String area,String street,String additionalDirections){ a.setRecipientName(recipientName); a.setPhoneNumber(phoneNumber); a.setCity(city); a.setLatitude(latitude); a.setLongitude(longitude); a.setArea(area); a.setStreet(street); a.setAdditionalDirections(additionalDirections); }
}
