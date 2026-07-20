package com.smart.ecommerce.report.dto;
import com.smart.ecommerce.report.util.ReportPeriod;import java.time.LocalDate;
public record CustomerSalesFilter(ReportPeriod period,LocalDate dateFrom,LocalDate dateTo,Long customerId,String customerName){}
