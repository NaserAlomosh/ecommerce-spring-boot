package com.smart.ecommerce.report.dto;
import com.smart.ecommerce.report.util.ReportPeriod;
import java.math.BigDecimal;
import java.time.LocalDate;
public record SalesOrderFilter(ReportPeriod period, LocalDate dateFrom,
                               LocalDate dateTo, String orderNumber,
                               Long customerId, String customerName,
                               BigDecimal minAmount, BigDecimal maxAmount,
                               Long productId, Long categoryId,
                               Long assignedDeliveryUserId, String currency) {}
