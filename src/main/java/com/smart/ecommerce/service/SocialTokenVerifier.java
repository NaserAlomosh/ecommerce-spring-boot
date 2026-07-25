package com.smart.ecommerce.service;

import com.smart.ecommerce.config.AuthProperties;
import com.smart.ecommerce.enums.SocialProvider;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SocialTokenVerifier {
  private static final String GOOGLE_ISSUER = "https://accounts.google.com";
  private static final String GOOGLE_JWKS = "https://www.googleapis.com/oauth2/v3/certs";
  private static final String APPLE_ISSUER = "https://appleid.apple.com";
  private static final String APPLE_JWKS = "https://appleid.apple.com/auth/keys";

  private final AuthProperties properties;
  private final Map<SocialProvider, JwtDecoder> decoders;

  @Autowired
  public SocialTokenVerifier(AuthProperties properties) {
    this(properties, createDecoders(properties));
  }

  SocialTokenVerifier(AuthProperties properties, Map<SocialProvider, JwtDecoder> decoders) {
    this.properties = properties;
    this.decoders = decoders;
  }

  public VerifiedSocialClaims verify(String token, SocialProvider provider, String nonce) {
    String audience = audience(provider);
    if (!StringUtils.hasText(audience)) {
      throw invalidToken();
    }

    try {
      Jwt jwt = decoders.get(provider).decode(token);
      if (StringUtils.hasText(nonce) && !nonce.equals(jwt.getClaimAsString("nonce"))) {
        throw invalidToken();
      }
      String subject = jwt.getSubject();
      String email = jwt.getClaimAsString("email");
      if (!StringUtils.hasText(subject) || !StringUtils.hasText(email)) {
        throw invalidToken();
      }
      return new VerifiedSocialClaims(
          subject,
          email,
          booleanClaim(jwt, "email_verified"),
          claimOrDefault(jwt, "given_name", "Customer"),
          claimOrDefault(jwt, "family_name", provider.name()));
    } catch (JwtException | IllegalArgumentException ex) {
      throw invalidToken();
    }
  }

  private String audience(SocialProvider provider) {
    return provider == SocialProvider.GOOGLE
        ? properties.googleAudience()
        : properties.appleAudience();
  }

  private static String claimOrDefault(Jwt jwt, String claim, String fallback) {
    String value = jwt.getClaimAsString(claim);
    return StringUtils.hasText(value) ? value : fallback;
  }

  private static boolean booleanClaim(Jwt jwt, String claim) {
    Object value = jwt.getClaim(claim);
    return Boolean.TRUE.equals(value) || "true".equalsIgnoreCase(String.valueOf(value));
  }

  private static Map<SocialProvider, JwtDecoder> createDecoders(AuthProperties properties) {
    Map<SocialProvider, JwtDecoder> result = new EnumMap<>(SocialProvider.class);
    result.put(
        SocialProvider.GOOGLE,
        decoder(GOOGLE_JWKS, GOOGLE_ISSUER, properties.googleAudience()));
    result.put(
        SocialProvider.APPLE, decoder(APPLE_JWKS, APPLE_ISSUER, properties.appleAudience()));
    return result;
  }

  private static JwtDecoder decoder(String jwks, String issuer, String audience) {
    NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwks).build();
    OAuth2TokenValidator<Jwt> audienceValidator =
        jwt ->
            StringUtils.hasText(audience) && jwt.getAudience().contains(audience)
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Invalid token audience", null));
    decoder.setJwtValidator(
        new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefaultWithIssuer(issuer), audienceValidator));
    return decoder;
  }

  private static BadCredentialsException invalidToken() {
    return new BadCredentialsException("error.invalid_provider_token");
  }

  public record VerifiedSocialClaims(
      String subject, String email, boolean emailVerified, String firstName, String lastName) {}
}
