package de.fiscalnorth.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ai.cron")
public record AiCronProperties(
        boolean enabled,
        String budgetAlerts,
        String optimization,
        String insights,
        String pruneRead,
        int pruneReadDays
) {
    public AiCronProperties {
        if (budgetAlerts == null || budgetAlerts.isBlank()) {
            budgetAlerts = "0 0 8,20 * * *";
        }
        if (optimization == null || optimization.isBlank()) {
            optimization = "0 0 6 * * *";
        }
        if (insights == null || insights.isBlank()) {
            insights = "0 30 12 * * *";
        }
        if (pruneRead == null || pruneRead.isBlank()) {
            pruneRead = "0 0 3 * * SUN";
        }
        if (pruneReadDays <= 0) {
            pruneReadDays = 30;
        }
    }
}
