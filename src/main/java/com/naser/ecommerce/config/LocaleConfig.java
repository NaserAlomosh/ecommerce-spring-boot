package com.naser.ecommerce.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class LocaleConfig {
    public static final Locale ENGLISH = Locale.ENGLISH;
    public static final Locale ARABIC = Locale.forLanguageTag("ar");
    private static final List<Locale> SUPPORTED_LOCALES = List.of(ENGLISH, ARABIC);
    private static final String ACCEPT_LANGUAGE = "Accept-Language";
    private static final String LEGACY_ACCEPT_LANGUAGE = "accept/language";

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver() {
            @Override
            public Locale resolveLocale(HttpServletRequest request) {
                String language = firstNonBlank(
                        request.getHeader(ACCEPT_LANGUAGE),
                        request.getHeader(LEGACY_ACCEPT_LANGUAGE)
                );
                if (language == null) {
                    return ENGLISH;
                }
                try {
                    Locale requestedLocale = Locale.lookup(Locale.LanguageRange.parse(language), SUPPORTED_LOCALES);
                    return requestedLocale == null ? ENGLISH : requestedLocale;
                } catch (IllegalArgumentException ex) {
                    return ENGLISH;
                }
            }
        };
        resolver.setDefaultLocale(ENGLISH);
        resolver.setSupportedLocales(SUPPORTED_LOCALES);
        return resolver;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setDefaultLocale(ENGLISH);
        return messageSource;
    }

    private String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        if (fallback != null && !fallback.isBlank()) {
            return fallback;
        }
        return null;
    }
}
