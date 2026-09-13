package com.smart.ecommerce.service;

import java.net.SocketTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
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

  @Value("${spring.mail.retry.max-attempts:2}") private int maxAttempts;

  @Value("${spring.mail.retry.backoff-millis:1000}") private long backoffMillis;

  @Override
  public void sendEmailVerificationOtp(String email, String name, String otp) {
    send(email, "Verify your email",
         "<h2>Email verification</h2><p>Hello " + name +
             ", use this code to verify your account.</p><h1>" + otp +
             "</h1>");
  }

  @Override
  public void sendPasswordResetOtp(String email, String name, String otp) {
    send(email, "Reset your password",
         "<h2>Password reset</h2><p>Hello " + name +
             ", use this code to reset your password.</p><h1>" + otp +
             "</h1>");
  }

  private void send(String to, String subject, String html) {
    int attempts = Math.max(1, maxAttempts);
    for (int attempt = 1; attempt <= attempts; attempt++) {
      try {
        sendOnce(to, subject, html);
        return;
      } catch (MailException ex) {
        if (!isTransientReadTimeout(ex) || attempt == attempts) {
          log.error(
              "Failed to send email '{}' to {} after {}/{} attempt(s). Check " +
              "SMTP_HOST, SMTP_PORT, SMTP_USERNAME, SMTP_PASSWORD, " +
              "SMTP_FROM, SMTP_TLS, SMTP_TIMEOUT, and SMTP_WRITE_TIMEOUT.",
              subject, to, attempt, attempts, ex);
          throw ex;
        }

        log.warn("Transient SMTP read timeout while sending email '{}' to {} " +
                 "on attempt {}/{}. Retrying after {} ms.",
                 subject, to, attempt, attempts, backoffMillis, ex);
        sleepBeforeRetry();
      } catch (Exception ex) {
        log.error("Failed to prepare email '{}' to {}", subject, to, ex);
        throw new IllegalStateException("Failed to send email", ex);
      }
    }
  }

  private void sendOnce(String to, String subject, String html)
      throws Exception {
    var message = mailSender.createMimeMessage();
    var helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setFrom(from);
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(html, true);
    mailSender.send(message);
    log.info("Sent email '{}' to {}", subject, to);
  }

  private boolean isTransientReadTimeout(MailException ex) {
    if (hasSocketTimeoutCause(ex)) {
      return true;
    }

    if (ex instanceof MailSendException mailSendException) {
      for (Exception messageException :
           mailSendException.getMessageExceptions()) {
        if (hasSocketTimeoutCause(messageException)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean hasSocketTimeoutCause(Throwable throwable) {
    Throwable current = throwable;
    while (current != null) {
      if (current instanceof SocketTimeoutException) {
        return true;
      }
      current = current.getCause();
    }
    return false;
  }

  private void sleepBeforeRetry() {
    if (backoffMillis <= 0) {
      return;
    }

    try {
      Thread.sleep(backoffMillis);
    } catch (InterruptedException interrupted) {
      Thread.currentThread().interrupt();
    }
  }
}
