package com.smart.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(boolean success, String message, T data,
                             Instant timestamp) {
  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(true, message, data, Instant.now());
  }

  public static <T> ApiResponse<T> failure(String message, T data) {
    return new ApiResponse<>(false, message, data, Instant.now());
  }
}
