package com.smart.ecommerce.service;
public interface EmailService{ void sendEmailVerificationOtp(String email,String name,String otp); void sendPasswordResetOtp(String email,String name,String otp); }
