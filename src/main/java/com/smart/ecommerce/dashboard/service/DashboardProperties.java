package com.smart.ecommerce.dashboard.service;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.dashboard")
public record DashboardProperties(@Min(0) int lowStockThreshold) {}
