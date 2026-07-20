package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.*;
import com.smart.ecommerce.dto.inventory.InventoryDtos.*;
import com.smart.ecommerce.enums.InventoryMovementType;
import com.smart.ecommerce.service.InventoryService;
import com.smart.ecommerce.util.MessageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor @RequestMapping("/api/v1/admin/inventory") @Tag(name="Inventory History", description="Append-only inventory audit trail for every stock movement.")
public class AdminInventoryController { private final InventoryService service; private final MessageUtil messages;
 @GetMapping("/history") @Operation(summary="Search inventory history", description="ADMIN only. Supports pagination, sorting, and filters by product, movement type, customer, order number, and created date range.")
 public ApiResponse<PaginationResponse<InventoryHistoryResponse>> history(@RequestParam(required=false) Long productId, @RequestParam(required=false) InventoryMovementType movementType, @RequestParam(required=false) Long customerId, @RequestParam(required=false) String orderNumber, @RequestParam(required=false) Instant fromDate, @RequestParam(required=false) Instant toDate, Pageable pageable){ return ApiResponse.success(messages.getMessage("inventory.history.loaded"), service.search(productId,movementType,customerId,orderNumber,fromDate,toDate,pageable)); }
 @PostMapping("/adjustments") @Operation(summary="Create manual admin stock adjustment", description="Records an ADMIN_ADJUSTMENT movement and rejects resulting negative stock.")
 public ApiResponse<Void> adjust(@Valid @RequestBody AdminAdjustmentRequest request){ service.recordAdminAdjustment(request.productId(), request.quantityChange(), request.note()); return ApiResponse.success(messages.getMessage("inventory.adjusted"), null); }
}
