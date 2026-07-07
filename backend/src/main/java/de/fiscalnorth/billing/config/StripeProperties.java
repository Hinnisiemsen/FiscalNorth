package de.fiscalnorth.billing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.stripe")
public class StripeProperties {

    private boolean enabled = false;
    private String secretKey = "";
    private String webhookSecret = "";
    private String priceIdMonthly = "";
    private String priceIdYearly = "";
    private int trialDays = 14;
    private int pastDueGraceDays = 3;
    private String portalReturnUrl = "http://localhost:4200/account";
    private String checkoutSuccessUrl = "http://localhost:4200/account?checkout=success";
    private String checkoutCancelUrl = "http://localhost:4200/account/upgrade?checkout=canceled";

    public boolean isConfigured() {
        return enabled
                && secretKey != null && !secretKey.isBlank()
                && webhookSecret != null && !webhookSecret.isBlank();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public String getPriceIdMonthly() {
        return priceIdMonthly;
    }

    public void setPriceIdMonthly(String priceIdMonthly) {
        this.priceIdMonthly = priceIdMonthly;
    }

    public String getPriceIdYearly() {
        return priceIdYearly;
    }

    public void setPriceIdYearly(String priceIdYearly) {
        this.priceIdYearly = priceIdYearly;
    }

    public int getTrialDays() {
        return trialDays;
    }

    public void setTrialDays(int trialDays) {
        this.trialDays = trialDays;
    }

    public int getPastDueGraceDays() {
        return pastDueGraceDays;
    }

    public void setPastDueGraceDays(int pastDueGraceDays) {
        this.pastDueGraceDays = pastDueGraceDays;
    }

    public String getPortalReturnUrl() {
        return portalReturnUrl;
    }

    public void setPortalReturnUrl(String portalReturnUrl) {
        this.portalReturnUrl = portalReturnUrl;
    }

    public String getCheckoutSuccessUrl() {
        return checkoutSuccessUrl;
    }

    public void setCheckoutSuccessUrl(String checkoutSuccessUrl) {
        this.checkoutSuccessUrl = checkoutSuccessUrl;
    }

    public String getCheckoutCancelUrl() {
        return checkoutCancelUrl;
    }

    public void setCheckoutCancelUrl(String checkoutCancelUrl) {
        this.checkoutCancelUrl = checkoutCancelUrl;
    }
}
