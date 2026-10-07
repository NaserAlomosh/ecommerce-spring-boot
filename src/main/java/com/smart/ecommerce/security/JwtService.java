package com.smart.ecommerce.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final JwtProperties properties;

  public JwtService(JwtProperties properties) { this.properties = properties; }

  public String extractUsername(String token) {
    return extractAllClaims(token).getSubject();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    return userDetails.getUsername().equals(extractUsername(token)) &&
        !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractAllClaims(token).getExpiration().before(new Date());
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(signingKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private SecretKey signingKey() {
    return Keys.hmacShaKeyFor(
        properties.secret().getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(com.smart.ecommerce.entity.User user) {
    return Jwts.builder()
        .subject(user.getEmail())
        .claims(Map.of("role", user.getRole().name(), "userId", user.getId()))
        .issuedAt(new Date())
        .expiration(expirationDate())
        .signWith(signingKey())
        .compact();
  }

  public long expirationMillis() { return properties.expirationMillis(); }

  public Date expirationDate() {
    return Date.from(Instant.now().plusMillis(properties.expirationMillis()));
  }
}
