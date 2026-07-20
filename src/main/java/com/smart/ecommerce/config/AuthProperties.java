package com.smart.ecommerce.config;
import java.time.Duration;import org.springframework.boot.context.properties.ConfigurationProperties;
@ConfigurationProperties(prefix="app.auth") public record AuthProperties(Duration refreshTokenExpiration,Duration otpExpiration,Duration otpResendCooldown,int otpMaxAttempts,Duration passwordResetTokenExpiration,String googleAudience,String appleAudience){}
