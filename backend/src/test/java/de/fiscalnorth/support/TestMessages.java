package de.fiscalnorth.support;

import de.fiscalnorth.shared.Messages;
import org.springframework.context.support.ResourceBundleMessageSource;

public final class TestMessages {

    private TestMessages() {
    }

    public static Messages create() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding("UTF-8");
        source.setFallbackToSystemLocale(false);
        return new Messages(source);
    }
}
