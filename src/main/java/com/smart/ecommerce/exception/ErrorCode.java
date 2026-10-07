package com.smart.ecommerce.exception;

public enum ErrorCode {
  VALIDATION_ERROR("error.validation"),
  BAD_REQUEST("error.bad_request"),
  UNAUTHORIZED("error.unauthorized"),
  FORBIDDEN("error.forbidden"),
  NOT_FOUND("error.not_found"),
  INTERNAL_ERROR("error.internal");

  private final String messageKey;

  ErrorCode(String messageKey) { this.messageKey = messageKey; }

  public String messageKey() { return messageKey; }
}
