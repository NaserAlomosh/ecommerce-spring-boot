package com.smart.ecommerce.service;

import com.smart.ecommerce.entity.User;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class CustomerContextService {
 private final UserRepository userRepository;
 public User currentCustomer() {
  String email = SecurityContextHolder.getContext().getAuthentication().getName();
  return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
 }
}
