package com.smart.ecommerce.repository;

import com.smart.ecommerce.entity.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository
    extends JpaRepository<InventoryMovement, Long> {}
