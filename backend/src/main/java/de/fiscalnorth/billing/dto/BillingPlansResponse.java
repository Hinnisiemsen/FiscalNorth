package de.fiscalnorth.billing.dto;

import java.util.List;

public record BillingPlansResponse(
        List<BillingPlanDto> plans,
        boolean billingEnabled
) {
}
