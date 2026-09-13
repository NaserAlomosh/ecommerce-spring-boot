package com.smart.ecommerce.repository;

import com.smart.ecommerce.entity.CustomerAddress;
import jakarta.persistence.LockModeType;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface CustomerAddressRepository
    extends JpaRepository<CustomerAddress, Long> {
  List<CustomerAddress>
  findByCustomerIdAndActiveTrueOrderByDefaultAddressDescCreatedAtDesc(
      Long customerId);
  Optional<CustomerAddress> findByIdAndCustomerIdAndActiveTrue(Long id,
                                                               Long customerId);
  Optional<CustomerAddress>
  findFirstByCustomerIdAndActiveTrueAndDefaultAddressTrue(Long customerId);
  Optional<CustomerAddress>
  findFirstByCustomerIdAndActiveTrueOrderByCreatedAtDesc(Long customerId);
  long countByCustomerIdAndActiveTrue(Long customerId);
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select a from CustomerAddress a where a.customer.id=:customerId " +
         "and a.active=true order by a.defaultAddress desc, a.createdAt desc")
  List<CustomerAddress>
  lockActiveByCustomerId(@Param("customerId") Long customerId);
  @Modifying @Query("update CustomerAddress a set a.defaultAddress=false where a.customer.id=:customerId and a.active=true and a.defaultAddress=true") int clearDefaults(@Param("customerId") Long customerId);
}
