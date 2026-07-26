package com.smart.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smart.ecommerce.entity.ContactMessage;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.repository.ContactMessageRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AdminContactMessageServiceTest {
  private static final Instant NOW = Instant.parse("2026-07-26T12:00:00Z");

  @Mock private ContactMessageRepository repository;
  private AdminContactMessageService service;

  @BeforeEach
  void setUp() {
    service =
        new AdminContactMessageService(
            repository, Clock.fixed(NOW, ZoneOffset.UTC));
  }

  @Test
  void listsUnreadMessagesNewestFirst() {
    ContactMessage message = message(7L, false);
    when(repository.findByRead(false, PageRequest.of(0, 20).withSort(
        org.springframework.data.domain.Sort.by(
            org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))))
        .thenReturn(new PageImpl<>(java.util.List.of(message)));

    var result = service.list(false, Pageable.ofSize(20));

    assertThat(result.content()).singleElement().satisfies(response -> {
      assertThat(response.id()).isEqualTo(7L);
      assertThat(response.read()).isFalse();
      assertThat(response.message()).isEqualTo("Please call me");
    });
  }

  @Test
  void marksMessageReadAndRecordsTimestamp() {
    ContactMessage message = message(7L, false);
    when(repository.findById(7L)).thenReturn(Optional.of(message));

    var response = service.updateReadStatus(7L, true);

    assertThat(response.read()).isTrue();
    assertThat(response.readAt()).isEqualTo(NOW);
    assertThat(message.isRead()).isTrue();
    verify(repository).findById(7L);
  }

  @Test
  void marksMessageUnreadAndClearsTimestamp() {
    ContactMessage message = message(7L, true);
    message.setReadAt(NOW.minusSeconds(60));
    when(repository.findById(7L)).thenReturn(Optional.of(message));

    var response = service.updateReadStatus(7L, false);

    assertThat(response.read()).isFalse();
    assertThat(response.readAt()).isNull();
  }

  @Test
  void rejectsUnknownMessage() {
    when(repository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.updateReadStatus(99L, true))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("contact_message.error.not_found");
  }

  private ContactMessage message(Long id, boolean read) {
    ContactMessage message = new ContactMessage();
    message.setId(id);
    message.setName("Jane");
    message.setEmail("jane@example.com");
    message.setSubject("Call request");
    message.setMessage("Please call me");
    message.setRead(read);
    message.setCreatedAt(NOW.minusSeconds(3600));
    return message;
  }
}
