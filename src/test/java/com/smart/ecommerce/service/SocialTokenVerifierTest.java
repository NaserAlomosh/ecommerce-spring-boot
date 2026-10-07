package com.smart.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.smart.ecommerce.config.AuthProperties;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SocialTokenVerifierTest {

  @Test
  void createsBeanUsingAuthPropertiesConstructor() {
    try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
      context.registerBean(
          AuthProperties.class,
          () ->
              new AuthProperties(
                  Duration.ofDays(30),
                  Duration.ofMinutes(10),
                  Duration.ofMinutes(1),
                  5,
                  Duration.ofMinutes(15),
                  "google-client-id",
                  "apple-client-id"));
      context.register(SocialTokenVerifier.class);

      context.refresh();

      assertThat(context.getBean(SocialTokenVerifier.class)).isNotNull();
    }
  }
}
