package de.fiscalnorth.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.ai")
public record AiProperties(
        @DefaultValue("true") boolean enabled,
        @DefaultValue("true") boolean demoFallback) {
}
