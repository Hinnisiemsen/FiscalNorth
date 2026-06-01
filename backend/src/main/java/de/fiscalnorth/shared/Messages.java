package de.fiscalnorth.shared;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class Messages {

    private final MessageSource messageSource;

    public Messages(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String get(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, code, locale);
    }

    public String getForLocale(Locale locale, String code, Object... args) {
        return messageSource.getMessage(code, args, code, locale);
    }

    public static Locale defaultCronLocale() {
        String tag = System.getProperty("fiscalnorth.default-locale", "en");
        return switch (tag.toLowerCase()) {
            case "de" -> Locale.GERMAN;
            case "fr" -> Locale.FRENCH;
            case "es" -> Locale.forLanguageTag("es");
            default -> Locale.ENGLISH;
        };
    }
}
