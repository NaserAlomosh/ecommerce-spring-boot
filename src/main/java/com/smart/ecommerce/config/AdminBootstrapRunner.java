package com.smart.ecommerce.config;

import com.smart.ecommerce.entity.User;
import com.smart.ecommerce.enums.Role;
import com.smart.ecommerce.enums.UserStatus;
import com.smart.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class AdminBootstrapRunner implements CommandLineRunner {
    private final AdminBootstrapProperties properties;
    private final UserRepository users;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (!properties.enabled() || users.countByRoleAndStatus(Role.ADMIN, UserStatus.ACTIVE) > 0) return;
        if (!StringUtils.hasText(properties.email()) || !StringUtils.hasText(properties.password())) {
            throw new IllegalStateException("error.bootstrap_admin_missing");
        }
        User admin = new User();
        admin.setFirstName(defaultValue(properties.firstName(), "System"));
        admin.setLastName(defaultValue(properties.lastName(), "Admin"));
        admin.setEmail(properties.email().trim().toLowerCase());
        admin.setPhoneNumber(defaultValue(properties.phoneNumber(), "+10000000000"));
        admin.setPasswordHash(encoder.encode(properties.password()));
        admin.setRole(Role.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        admin.setEmailVerified(true);
        users.save(admin);
    }

    private String defaultValue(String value, String fallback) { return StringUtils.hasText(value) ? value : fallback; }
}
