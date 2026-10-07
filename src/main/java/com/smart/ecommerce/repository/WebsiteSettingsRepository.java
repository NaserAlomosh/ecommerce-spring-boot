package com.smart.ecommerce.repository;

import com.smart.ecommerce.entity.WebsiteSettings;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebsiteSettingsRepository
    extends JpaRepository<WebsiteSettings, Long> {
  Optional<WebsiteSettings> findBySettingsKey(String settingsKey);
}
