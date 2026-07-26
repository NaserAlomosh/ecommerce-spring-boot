package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.PaginationResponse;
import com.smart.ecommerce.dto.contact.ContactMessageDtos.AdminContactMessageResponse;
import com.smart.ecommerce.dto.contact.ContactMessageDtos.UnreadMessageCountResponse;
import com.smart.ecommerce.entity.ContactMessage;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.repository.ContactMessageRepository;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminContactMessageService {
  private final ContactMessageRepository repository;
  private final Clock clock;

  @Transactional(readOnly = true)
  public UnreadMessageCountResponse unreadCount() {
    return new UnreadMessageCountResponse(repository.countByRead(false));
  }

  @Transactional(readOnly = true)
  public PaginationResponse<AdminContactMessageResponse> list(
      Boolean read, Pageable pageable) {
    Pageable newestFirst =
        PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<ContactMessage> messages =
        read == null
            ? repository.findAll(newestFirst)
            : repository.findByRead(read, newestFirst);
    return PaginationResponse.from(messages.map(this::toResponse));
  }

  @Transactional
  public AdminContactMessageResponse updateReadStatus(Long id, boolean read) {
    ContactMessage message =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "contact_message.error.not_found"));
    if (message.isRead() != read) {
      message.setRead(read);
      message.setReadAt(read ? Instant.now(clock) : null);
    }
    return toResponse(message);
  }

  private AdminContactMessageResponse toResponse(ContactMessage message) {
    return new AdminContactMessageResponse(
        message.getId(),
        message.getName(),
        message.getEmail(),
        message.getPhone(),
        message.getSubject(),
        message.getMessage(),
        message.isRead(),
        message.getReadAt(),
        message.getCreatedAt());
  }
}
