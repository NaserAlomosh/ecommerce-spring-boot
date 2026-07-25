package com.smart.ecommerce.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.smart.ecommerce.dto.product.ProductDtos.ProductUpdateRequest;
import com.smart.ecommerce.service.ProductService;
import com.smart.ecommerce.util.MessageUtil;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminProductController.class)
class AdminProductControllerTest {
  @Autowired MockMvc mvc;
  @MockBean ProductService productService;
  @MockBean MessageUtil messages;

  @Test
  void updateAcceptsProductAndImagesAsMultipart() throws Exception {
    MockMultipartFile product = new MockMultipartFile(
        "product", "", MediaType.APPLICATION_JSON_VALUE,
        productJson().getBytes(StandardCharsets.UTF_8));
    MockMultipartFile image = new MockMultipartFile(
        "images", "product.png", MediaType.IMAGE_PNG_VALUE,
        new byte[] {1, 2, 3});
    when(messages.getMessage("admin.product_updated"))
        .thenReturn("Product updated successfully");

    mvc.perform(multipart("/api/v1/admin/products/{productId}", 42L)
                    .file(product)
                    .file(image)
                    .with(request -> {
                      request.setMethod("PUT");
                      return request;
                    })
                    .with(user("admin").roles("ADMIN"))
                    .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message")
                       .value("Product updated successfully"));

    verify(productService)
        .update(eq(42L), any(ProductUpdateRequest.class),
                argThat(images -> images.size() == 1 &&
                                  "product.png".equals(
                                      images.getFirst().getOriginalFilename())));
  }

  private String productJson() {
    return """
        {
          "categoryId": 1,
          "nameEn": "Headphones",
          "nameAr": "سماعات",
          "sku": "HP-1",
          "price": 49.99,
          "currency": "JOD",
          "stockQuantity": 20,
          "lowStockThreshold": 5,
          "active": true,
          "featured": false
        }
        """;
  }
}
