package com.smart.ecommerce.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailService implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:${spring.mail.username:no-reply@example.com}}")
    private String from;

    @Override
    public void sendEmailVerificationOtp(String email, String name, String otp) {
        send(
                email,
                "Verify your email",
                "<h2>Email verification</h2><p>Hello " + name
                        + ", use this code to verify your account.</p><h1>" + otp + "</h1>");
    }

    @Override
    public void sendPasswordResetOtp(String email, String name, String otp) {
        send(
                email,
                "Reset your password",
                "<h2>Password reset</h2><p>Hello " + name
                        + ", use this code to reset your password.</p><h1>" + otp + "</h1>");
    }

    private void send(String to, String subject, String html) {
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Sent email '{}' to {}", subject, to);
        } catch (MailException ex) {
            log.error("Failed to send email '{}' to {}. Check SMTP_HOST, SMTP_PORT, SMTP_USERNAME, SMTP_PASSWORD, SMTP_FROM, and SMTP_TLS.", subject, to, ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to prepare email '{}' to {}", subject, to, ex);
            throw new IllegalStateException("Failed to send email", ex);
        }
    }
}
