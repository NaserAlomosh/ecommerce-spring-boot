package com.smart.ecommerce.exception;

import java.time.Instant;
import java.util.Map;

public record ApiError(ErrorCode code, String message,
                       Map<String, String> errors, Instant timestamp) {
  public static ApiError of(ErrorCode code, String message,
                            Map<String, String> errors) {
    return new ApiError(code, message, errors, Instant.now());
  }
}
