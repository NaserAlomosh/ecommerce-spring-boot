package com.smart.ecommerce.repository;

import com.smart.ecommerce.entity.GuestOrderLink;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestOrderLinkRepository
    extends JpaRepository<GuestOrderLink, Long> {
  Optional<GuestOrderLink> findBySlug(String slug);
  boolean existsBySlug(String slug);
  boolean existsBySlugAndIdNot(String slug, Long id);
}
