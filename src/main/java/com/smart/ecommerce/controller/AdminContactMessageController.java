package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.PaginationResponse;
import com.smart.ecommerce.dto.contact.ContactMessageDtos.AdminContactMessageResponse;
import com.smart.ecommerce.dto.contact.ContactMessageDtos.UpdateReadStatusRequest;
import com.smart.ecommerce.dto.contact.ContactMessageDtos.UnreadMessageCountResponse;
import com.smart.ecommerce.service.AdminContactMessageService;
import com.smart.ecommerce.util.MessageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/contact/messages")
@PreAuthorize("hasAnyRole('ADMIN','SUB_ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminContactMessageController {
  private final AdminContactMessageService service;
  private final MessageUtil messages;

  @Operation(summary = "List contact messages, optionally filtered by read status")
  @GetMapping
  public ApiResponse<PaginationResponse<AdminContactMessageResponse>> list(
      @RequestParam(required = false) Boolean read, Pageable pageable) {
    return ApiResponse.success(
        messages.getMessage("admin.contact_messages.loaded"),
        service.list(read, pageable));
  }

  @Operation(summary = "Get the number of unread contact messages")
  @GetMapping("/unread-count")
  public ApiResponse<UnreadMessageCountResponse> unreadCount() {
    return ApiResponse.success(
        messages.getMessage("admin.contact_messages.unread_count_loaded"),
        service.unreadCount());
  }

  @Operation(
      summary = "Mark a contact message as read or unread",
      description = "Send {\"read\":true} to mark as read or {\"read\":false} to mark as unread.")
  @PatchMapping("/{id}/read-status")
  public ApiResponse<AdminContactMessageResponse> updateReadStatus(
      @PathVariable Long id,
      @Valid @RequestBody UpdateReadStatusRequest request) {
    return ApiResponse.success(
        messages.getMessage("admin.contact_message.status_updated"),
        service.updateReadStatus(id, request.read()));
  }
}
