package de.fiscalnorth.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.auth")
public class AuthProperties {
    private String frontendUrl = "http://localhost:4200";
    private String loginSuccessUrl = "http://localhost:4200/";
}
