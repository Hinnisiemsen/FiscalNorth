package de.fiscalnorth.xs2a.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration for Berlin Group XS2A / finAPI integration.
 * Enable by setting base-url and access-token (OAuth Bearer token from finAPI).
 */
@Component
@ConfigurationProperties(prefix = "app.xs2a")
public class Xs2aProperties {

    /**
     * Enable XS2A bank sync. Requires base-url and access-token when true.
     */
    private boolean enabled = false;

    /**
     * Base URL of the XS2A API (e.g. https://xs2a-sandbox.finapi.io for finAPI sandbox).
     */
    private String baseUrl = "";

    /**
     * OAuth2 Bearer token for API authentication.
     * Obtain via finAPI OAuth flow (client credentials or user token).
     */
    private String accessToken = "";

    /**
     * PSU-ID (Payment Service User ID). Required by finAPI for security.
     * Typically the end-user identifier in your system.
     */
    private String psuId = "default-user";

    /**
     * Redirect URI after SCA at bank. Must be whitelisted in finAPI.
     */
    private String redirectUri = "http://localhost:4200/bank-sync/callback";

    public boolean isEnabled() {
        return enabled && baseUrl != null && !baseUrl.isBlank() && accessToken != null && !accessToken.isBlank();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getPsuId() {
        return psuId;
    }

    public void setPsuId(String psuId) {
        this.psuId = psuId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
