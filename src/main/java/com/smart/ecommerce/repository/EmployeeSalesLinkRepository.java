package com.smart.ecommerce.repository;

import com.smart.ecommerce.entity.EmployeeSalesLink;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeSalesLinkRepository
    extends JpaRepository<EmployeeSalesLink, Long> {
  @EntityGraph(attributePaths = "employee")
  Optional<EmployeeSalesLink> findByTokenHash(String tokenHash);
}
