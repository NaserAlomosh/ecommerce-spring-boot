package com.smart.ecommerce.dto.inventory;

import com.smart.ecommerce.enums.InventoryMovementType;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public final class InventoryDtos {
  private InventoryDtos() {}
  public record AdminAdjustmentRequest(@NotNull Long productId,
                                       int quantityChange, String note) {}
  public record ProductSummary(Long id, String nameEn, String nameAr,
                               String sku) {}
  public record UserSummary(Long id, String name) {}
  public record InventoryHistoryResponse(
      InventoryMovementType movementType, String description,
      ProductSummary product, String orderNumber, Long customerId,
      String customerName, int quantityBefore, int quantityChange,
      int quantityAfter, UserSummary performedBy, String note,
      Instant createdAt) {}
}
