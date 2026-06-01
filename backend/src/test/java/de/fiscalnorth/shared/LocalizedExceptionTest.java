package de.fiscalnorth.shared;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class LocalizedExceptionTest {

    @Test
    void messagesResolvesLocalizedExceptionCode() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding("UTF-8");
        source.setFallbackToSystemLocale(false);
        Messages messages = new Messages(source);

        String text = messages.getForLocale(Locale.GERMAN, "error.bankAccount.duplicate");

        assertThat(text).contains("IBAN");
    }

    @Test
    void localizedExceptionCarriesCodeAndArgs() {
        LocalizedException ex = new LocalizedException("error.currency.unknown", "CHF");

        assertThat(ex.getMessageCode()).isEqualTo("error.currency.unknown");
        assertThat(ex.getMessageArgs()).containsExactly("CHF");
    }
}
