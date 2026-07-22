package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.inventory.InventoryDtos.*;
import com.smart.ecommerce.entity.*;
import com.smart.ecommerce.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryMapper {
  private final MessageUtil messages;
  public InventoryHistoryResponse toResponse(InventoryHistory h) {
    Product p = h.getProduct();
    User u = h.getPerformedBy();
    return new InventoryHistoryResponse(
        h.getMovementType(),
        messages.getMessage("inventory.movement." +
                            h.getMovementType().name().toLowerCase()),
        new ProductSummary(p.getId(), p.getNameEn(), p.getNameAr(), p.getSku()),
        h.getOrderNumber(), h.getCustomerId(), h.getCustomerNameSnapshot(),
        h.getQuantityBefore(), h.getQuantityChange(), h.getQuantityAfter(),
        u == null ? null
                  : new UserSummary(u.getId(),
                                    u.getFirstName() + " " + u.getLastName()),
        h.getNote(), h.getCreatedAt());
  }
}
