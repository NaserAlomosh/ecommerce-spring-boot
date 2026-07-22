package com.smart.ecommerce.service;

import com.smart.ecommerce.repository.OrderRepository;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderNumberGenerator {
  private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
  private static final int MAX = 20;
  private final SecureRandom random = new SecureRandom();
  private final OrderRepository orders;
  public String generate() {
    for (int i = 0; i < MAX; i++) {
      String n = "ORD-" +
                 LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) +
                 "-" + suffix();
      if (!orders.existsByOrderNumber(n))
        return n;
    }
    throw new IllegalArgumentException(
        "Duplicate order number after exhausted retries");
  }
  private String suffix() {
    StringBuilder b = new StringBuilder(6);
    for (int i = 0; i < 6; i++)
      b.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
    return b.toString();
  }
}
