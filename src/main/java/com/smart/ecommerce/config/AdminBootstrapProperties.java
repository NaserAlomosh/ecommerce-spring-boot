package com.smart.ecommerce.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.bootstrap.admin")
public record AdminBootstrapProperties(
        boolean enabled,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String password) {}
