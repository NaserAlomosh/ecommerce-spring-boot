package com.smart.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smart.ecommerce.config.WebsiteContentProperties;
import com.smart.ecommerce.dto.website.WebsiteDtos.ContactMessageRequest;
import com.smart.ecommerce.entity.ContactMessage;
import com.smart.ecommerce.repository.ContactMessageRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WebsiteServiceTest {
  @Mock private ContactMessageRepository repository;

  @Test
  void returnsConfiguredCompanyAndContactDetails() {
    var properties =
        new WebsiteContentProperties(
            new WebsiteContentProperties.Company("Smart", "About", "عنا", "/logo.png"),
            new WebsiteContentProperties.Contact(
                "help@example.com", "+962", "Amman", "عمان"));
    var service = new WebsiteService(properties, repository);

    assertThat(service.company().name()).isEqualTo("Smart");
    assertThat(service.company().descriptionAr()).isEqualTo("عنا");
    assertThat(service.contact().email()).isEqualTo("help@example.com");
    assertThat(service.contact().addressAr()).isEqualTo("عمان");
  }

  @Test
  void trimsAndPersistsContactMessage() {
    var service =
        new WebsiteService(new WebsiteContentProperties(null, null), repository);
    Instant submittedAt = Instant.parse("2026-07-25T12:00:00Z");
    when(repository.save(any(ContactMessage.class)))
        .thenAnswer(
            invocation -> {
              ContactMessage message = invocation.getArgument(0);
              message.setId(42L);
              message.setCreatedAt(submittedAt);
              return message;
            });

    var response =
        service.submit(
            new ContactMessageRequest(
                " Jane Doe ", " JANE@EXAMPLE.COM ", "  ", " Question ", " Hello "));

    ArgumentCaptor<ContactMessage> captor = ArgumentCaptor.forClass(ContactMessage.class);
    verify(repository).save(captor.capture());
    assertThat(captor.getValue().getName()).isEqualTo("Jane Doe");
    assertThat(captor.getValue().getEmail()).isEqualTo("jane@example.com");
    assertThat(captor.getValue().getPhone()).isNull();
    assertThat(response.id()).isEqualTo(42L);
    assertThat(response.submittedAt()).isEqualTo(submittedAt);
  }
}
