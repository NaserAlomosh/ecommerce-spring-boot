package com.smart.ecommerce.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.smart.ecommerce.dto.auth.AuthDtos.LoginRequest;
import com.smart.ecommerce.dto.auth.AuthDtos.TokenResponse;
import com.smart.ecommerce.service.AuthService;
import com.smart.ecommerce.util.MessageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LegacyAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class LegacyAuthControllerTest {
  @Autowired MockMvc mvc;
  @MockBean AuthService auth;
  @MockBean MessageUtil messages;

  @Test
  void loginEndpointDelegatesToAuthService() throws Exception {
    when(messages.getMessage("auth.logged_in"))
        .thenReturn("Logged in successfully");
    when(auth.login(any(LoginRequest.class), eq("127.0.0.1")))
        .thenReturn(new TokenResponse("access-token", "refresh-token", 60000));

    mvc.perform(post("/api/v1/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"naser@example.com\","
                             + "\"password\":\"Naser123\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Logged in successfully"))
        .andExpect(jsonPath("$.data.accessToken").value("access-token"))
        .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));
  }
}
