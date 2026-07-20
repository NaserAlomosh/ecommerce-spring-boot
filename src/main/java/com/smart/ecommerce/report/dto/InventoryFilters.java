package com.smart.ecommerce.report.dto;
import com.smart.ecommerce.enums.InventoryMovementType;import com.smart.ecommerce.report.dto.InventoryReportDtos.*;import com.smart.ecommerce.report.util.ReportPeriod;import java.math.BigDecimal;import java.time.*;
public final class InventoryFilters{private InventoryFilters(){}
 public record Current(Long productId,String productName,Long categoryId,Boolean active,StockStatus stockStatus,Integer minStock,Integer maxStock,BigDecimal minUnitCost,BigDecimal maxUnitCost){}
 public record Period(ReportPeriod period,LocalDate dateFrom,LocalDate dateTo){}
 public record Movement(Long productId,Long categoryId,InventoryMovementType movementType,String orderNumber,Long customerId,Long performedByUserId,ReportPeriod period,LocalDate dateFrom,LocalDate dateTo,Integer minQuantityChange,Integer maxQuantityChange,MovementDirection direction){}
}
