package com.smart.ecommerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI ecommerceOpenApi() {
    return new OpenAPI().info(
        new Info()
            .title("E-commerce API")
            .version("v1")
            .description("Production-ready e-commerce backend foundation"));
  }
}
