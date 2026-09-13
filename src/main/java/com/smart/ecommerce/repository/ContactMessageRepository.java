package com.smart.ecommerce.repository;

import com.smart.ecommerce.entity.ContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
  Page<ContactMessage> findByRead(boolean read, Pageable pageable);

  long countByRead(boolean read);
}
