package com.smart.ecommerce.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ApiResponseTest {

  @Test
  void successCreatesStandardResponse() {
    ApiResponse<String> response = ApiResponse.success("ok", "payload");

    assertThat(response.success()).isTrue();
    assertThat(response.message()).isEqualTo("ok");
    assertThat(response.data()).isEqualTo("payload");
    assertThat(response.timestamp()).isNotNull();
  }
}
