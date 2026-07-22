package com.smart.ecommerce.config;

import com.smart.ecommerce.dashboard.service.DashboardProperties;
import com.smart.ecommerce.report.service.BusinessTimeProperties;
import com.smart.ecommerce.report.service.ReportProperties;
import com.smart.ecommerce.repository.UserRepository;
import com.smart.ecommerce.security.JwtAuthenticationFilter;
import com.smart.ecommerce.security.JwtProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(
    {JwtProperties.class, AuthProperties.class, FileStorageProperties.class,
     AdminBootstrapProperties.class, DashboardProperties.class,
     ReportProperties.class, BusinessTimeProperties.class})
public class SecurityConfig {
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain
  securityFilterChain(HttpSecurity http,
                      AuthenticationProvider authenticationProvider)
      throws Exception {
    return http.cors(cors -> {})
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            a
            -> a.requestMatchers(HttpMethod.OPTIONS, "/**")
                   .permitAll()
                   .requestMatchers("/api/v1/auth/**", "/api/v1/login",
                                    "/api/v1/health", "/v3/api-docs/**",
                                    "/swagger-ui/**", "/swagger-ui.html", "/uploads/**")
                   .permitAll()
                   .requestMatchers(HttpMethod.GET, "/api/v1/categories/**",
                                    "/api/v1/products", "/api/v1/products/*",
                                    "/api/v1/products/*/reviews",
                                    "/api/v1/products/*/rating-summary")
                   .permitAll()
                   .requestMatchers("/api/v1/products/*/inventory-history")
                   .hasAnyRole("ADMIN", "SUB_ADMIN")
                   .requestMatchers("/api/v1/admin/**")
                   .hasAnyRole("ADMIN", "SUB_ADMIN")
                   .requestMatchers("/api/v1/customer/cart/**",
                                    "/api/v1/customer/wishlist/**",
                                    "/api/v1/customer/addresses/**",
                                    "/api/v1/customer/orders/**",
                                    "/api/v1/customers/me/reviews",
                                    "/api/v1/reviews/**")
                   .hasRole("CUSTOMER")
                   .requestMatchers("/api/v1/delivery/**")
                   .hasRole("DELIVERY")
                   .requestMatchers("/api/v1/users/**")
                   .authenticated()
                   .anyRequest()
                   .authenticated())
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter,
                         UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider(UserDetailsService uds,
                                                       PasswordEncoder pe) {
    DaoAuthenticationProvider p = new DaoAuthenticationProvider();
    p.setUserDetailsService(uds);
    p.setPasswordEncoder(pe);
    return p;
  }

  @Bean
  public UserDetailsService userDetailsService(UserRepository repo) {
    return email -> {
      var u = repo.findByEmail(email.toLowerCase())
                  .orElseThrow(
                      () -> new UsernameNotFoundException("User not found"));
      return org.springframework.security.core.userdetails.User
          .withUsername(u.getEmail())
          .password(u.getPasswordHash())
          .disabled(u.getStatus() !=
                    com.smart.ecommerce.enums.UserStatus.ACTIVE)
          .authorities("ROLE_" + u.getRole().name())
          .build();
    };
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(
        List.of("http://localhost:*", "http://127.0.0.1:*",
                "https://localhost:*", "https://127.0.0.1:*"));
    configuration.setAllowedMethods(
        List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setExposedHeaders(List.of("Authorization"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source =
        new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
