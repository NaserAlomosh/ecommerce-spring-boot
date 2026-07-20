package com.naser.ecommerce.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.LocaleResolver;

class LocaleConfigTest {

    private final LocaleResolver localeResolver = new LocaleConfig().localeResolver();

    @Test
    void resolvesArabicFromAcceptLanguageHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Accept-Language", "ar");

        Locale locale = localeResolver.resolveLocale(request);

        assertThat(locale.getLanguage()).isEqualTo("ar");
    }

    @Test
    void resolvesEnglishFromLegacyAcceptLanguageHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("accept/language", "en");

        Locale locale = localeResolver.resolveLocale(request);

        assertThat(locale).isEqualTo(Locale.ENGLISH);
    }
}
