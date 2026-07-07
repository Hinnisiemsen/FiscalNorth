package de.fiscalnorth.billing.dto;

public record BillingPlanDto(
        String id,
        String name,
        String priceId,
        String interval,
        int trialDays
) {
}
