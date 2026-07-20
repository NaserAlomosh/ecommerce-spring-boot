package com.smart.ecommerce.util;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import jakarta.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class MessageUtil {
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    private final HttpServletRequest request;

    public String getMessage(String key, Object... args) {
        Locale locale = localeResolver.resolveLocale(request);
        try {
            return messageSource.getMessage(key, args, locale);
        } catch (NoSuchMessageException ex) {
            return key;
        }
    }
}
