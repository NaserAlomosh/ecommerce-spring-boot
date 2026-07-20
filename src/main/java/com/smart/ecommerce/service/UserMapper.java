package com.smart.ecommerce.service;
import com.smart.ecommerce.dto.user.UserDtos.UserResponse;import com.smart.ecommerce.entity.User;import org.springframework.stereotype.Component;
@Component public class UserMapper{ public UserResponse toResponse(User u){return new UserResponse(u.getId(),u.getFirstName(),u.getLastName(),u.getEmail(),u.getPhoneNumber(),u.getRole(),u.getStatus(),u.isEmailVerified(),u.isPhoneVerified(),u.getProfileImage(),u.getLastLoginAt(),u.getCreatedAt(),u.getUpdatedAt());}}
