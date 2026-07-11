package de.fiscalnorth.billing.dto;

import de.fiscalnorth.billing.model.SubscriptionPlan;
import de.fiscalnorth.billing.model.SubscriptionStatus;

import java.time.Instant;
import java.util.List;

public record SubscriptionSummaryDto(
        SubscriptionPlan plan,
        SubscriptionStatus status,
        List<String> entitlements,
        Instant currentPeriodEnd,
        Instant trialEnd,
        boolean cancelAtPeriodEnd,
        boolean premiumActive,
        boolean billingEnabled,
        boolean premiumPreviewEnabled,
        boolean paidSubscriptionActive
) {
}
