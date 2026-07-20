package com.smart.ecommerce.report.service;
import java.time.ZoneId;import org.springframework.boot.context.properties.ConfigurationProperties;
@ConfigurationProperties(prefix="app")
public record BusinessTimeProperties(String businessTimeZone){ public ZoneId zoneId(){return ZoneId.of(businessTimeZone==null||businessTimeZone.isBlank()?"Asia/Amman":businessTimeZone);} }
