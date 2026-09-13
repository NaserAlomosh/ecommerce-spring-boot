package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.inventory.InventoryDtos.InventoryHistoryResponse;
import com.smart.ecommerce.service.InventoryService;
import com.smart.ecommerce.util.MessageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Tag(name = "Product Inventory History")
public class ProductInventoryHistoryController {
  private final InventoryService service;
  private final MessageUtil messages;
  @GetMapping("/{id}/inventory-history")
  @Operation(
      summary = "Get product inventory history",
      description = "Returns the complete append-only inventory history for " +
                    "one product. ADMIN only by security configuration.")
  public ApiResponse<List<InventoryHistoryResponse>>
  productHistory(@PathVariable Long id) {
    return ApiResponse.success(messages.getMessage("inventory.history.loaded"),
                               service.productHistory(id));
  }
}
