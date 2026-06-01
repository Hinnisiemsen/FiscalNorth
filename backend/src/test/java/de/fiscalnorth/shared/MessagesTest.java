package de.fiscalnorth.shared;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class MessagesTest {

    @AfterEach
    void tearDown() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void get_returnsGermanMessageWhenLocaleIsGerman() {
        Messages messages = new Messages(messageSource());
        LocaleContextHolder.setLocale(Locale.GERMAN);

        String text = messages.get("error.notFound", "Budget", "id", 42);

        assertThat(text).contains("nicht gefunden");
        assertThat(text).contains("Budget");
    }

    @Test
    void getForLocale_returnsFrenchMessage() {
        Messages messages = new Messages(messageSource());

        String text = messages.getForLocale(Locale.FRENCH, "error.unexpected");

        assertThat(text).contains("erreur inattendue");
    }

    private static ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding("UTF-8");
        return source;
    }
}
