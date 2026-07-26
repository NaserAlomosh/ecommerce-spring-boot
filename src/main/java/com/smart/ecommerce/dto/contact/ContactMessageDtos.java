package com.smart.ecommerce.dto.contact;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public final class ContactMessageDtos {
  private ContactMessageDtos() {}

  public record UnreadMessageCountResponse(long unreadCount) {}

  public record AdminContactMessageResponse(
      Long id,
      String name,
      String email,
      String phone,
      String subject,
      String message,
      boolean read,
      Instant readAt,
      Instant submittedAt) {}

  /** Set {@code read} to true to mark a message read, or false to mark it unread. */
  public record UpdateReadStatusRequest(@NotNull Boolean read) {}
}
