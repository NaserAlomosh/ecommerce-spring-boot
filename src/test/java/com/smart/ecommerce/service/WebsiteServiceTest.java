package com.smart.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smart.ecommerce.config.WebsiteContentProperties;
import com.smart.ecommerce.dto.website.WebsiteDtos.ContactMessageRequest;
import com.smart.ecommerce.dto.website.WebsiteDtos.CompanyUpdateRequest;
import com.smart.ecommerce.dto.website.WebsiteDtos.ContactUpdateRequest;
import com.smart.ecommerce.entity.ContactMessage;
import com.smart.ecommerce.entity.WebsiteSettings;
import com.smart.ecommerce.repository.ContactMessageRepository;
import com.smart.ecommerce.repository.WebsiteSettingsRepository;
import com.smart.ecommerce.storage.FileStorageService;
import com.smart.ecommerce.storage.StoredFile;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class WebsiteServiceTest {
  @Mock private ContactMessageRepository repository;
  @Mock private WebsiteSettingsRepository settingsRepository;
  @Mock private FileStorageService storageService;

  @Test
  void returnsConfiguredCompanyAndContactDetails() {
    var properties =
        new WebsiteContentProperties(
            new WebsiteContentProperties.Company(
                "Smart", "About", "عنا", "/logo.png", "Ahmad", "أحمد",
                "/owner.png", "https://maps.example.com", "Amman HQ",
                "https://instagram.com/smart", "https://facebook.com/smart",
                "https://tiktok.com/@smart", "https://linkedin.com/company/smart"),
            new WebsiteContentProperties.Contact(
                "help@example.com", "+962", "Amman", "عمان"));
    var service = new WebsiteService(properties, repository, settingsRepository,
                                     storageService);

    assertThat(service.company().name()).isEqualTo("Smart");
    assertThat(service.company().descriptionAr()).isEqualTo("عنا");
    assertThat(service.company().ownerNameEn()).isEqualTo("Ahmad");
    assertThat(service.company().ownerNameAr()).isEqualTo("أحمد");
    assertThat(service.company().ownerImageUrl()).isEqualTo("/owner.png");
    assertThat(service.company().locationUrl()).isEqualTo("https://maps.example.com");
    assertThat(service.company().locationName()).isEqualTo("Amman HQ");
    assertThat(service.company().instagramUrl()).isEqualTo("https://instagram.com/smart");
    assertThat(service.company().facebookUrl()).isEqualTo("https://facebook.com/smart");
    assertThat(service.company().tiktokUrl()).isEqualTo("https://tiktok.com/@smart");
    assertThat(service.company().linkedinUrl()).isEqualTo("https://linkedin.com/company/smart");
    assertThat(service.company().active()).isTrue();
    assertThat(service.contact().email()).isEqualTo("help@example.com");
    assertThat(service.contact().addressAr()).isEqualTo("عمان");
    assertThat(service.contact().active()).isTrue();
  }

  @Test
  void trimsAndPersistsContactMessage() {
    var service =
        new WebsiteService(new WebsiteContentProperties(null, null), repository,
                           settingsRepository, storageService);
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

  @Test
  void updatesCompanyAndPersistsActiveStatus() {
    var service = new WebsiteService(new WebsiteContentProperties(null, null),
                                     repository, settingsRepository,
                                     storageService);
    when(settingsRepository.save(any(WebsiteSettings.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var response = service.updateCompany(
        new CompanyUpdateRequest(" Smart ", " About ", " عنا ", null, null,
                                 null, null, null, null, null, null, false),
        null, null);

    ArgumentCaptor<WebsiteSettings> captor =
        ArgumentCaptor.forClass(WebsiteSettings.class);
    verify(settingsRepository).save(captor.capture());
    assertThat(captor.getValue().getCompanyName()).isEqualTo("Smart");
    assertThat(captor.getValue().getCompanyDescriptionEn()).isEqualTo("About");
    assertThat(response.active()).isFalse();
  }

  @Test
  void updatesContactAndNormalizesValues() {
    var service = new WebsiteService(new WebsiteContentProperties(null, null),
                                     repository, settingsRepository,
                                     storageService);
    when(settingsRepository.save(any(WebsiteSettings.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var response = service.updateContact(new ContactUpdateRequest(
        " HELP@EXAMPLE.COM ", " +962 ", " Amman ", " عمان ", false));

    assertThat(response.email()).isEqualTo("help@example.com");
    assertThat(response.phone()).isEqualTo("+962");
    assertThat(response.addressEn()).isEqualTo("Amman");
    assertThat(response.active()).isFalse();
  }

  @Test
  void uploadsNewCompanyImagesAndReturnsTheirUrls() {
    var service = new WebsiteService(new WebsiteContentProperties(null, null),
                                     repository, settingsRepository,
                                     storageService);
    var logo = new MockMultipartFile("logo", "logo.png", "image/png",
                                     new byte[] {1});
    var owner = new MockMultipartFile("ownerImage", "owner.png", "image/png",
                                      new byte[] {2});
    when(storageService.storeWebsiteImage(logo))
        .thenReturn(new StoredFile("https://cdn/logo.png", "website/logo",
                                   "image/png", 1));
    when(storageService.storeWebsiteImage(owner))
        .thenReturn(new StoredFile("https://cdn/owner.png", "website/owner",
                                   "image/png", 1));
    when(settingsRepository.save(any(WebsiteSettings.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var response = service.updateCompany(
        new CompanyUpdateRequest("Smart", null, null, null, null, null, null,
                                 null, null, null, null, true),
        logo, owner);

    assertThat(response.logoUrl()).isEqualTo("https://cdn/logo.png");
    assertThat(response.ownerImageUrl()).isEqualTo("https://cdn/owner.png");
  }
}
