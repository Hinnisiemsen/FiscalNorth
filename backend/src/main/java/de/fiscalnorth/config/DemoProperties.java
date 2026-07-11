package de.fiscalnorth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.demo")
public class DemoProperties {
    /** When true, all users receive premium entitlements without Stripe (local/staging demo). */
    private boolean premiumPreviewEnabled = true;
}
