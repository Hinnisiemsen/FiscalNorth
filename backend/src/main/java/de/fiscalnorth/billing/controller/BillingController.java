package de.fiscalnorth.billing.controller;

import com.stripe.exception.StripeException;
import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.billing.dto.BillingPlanDto;
import de.fiscalnorth.billing.dto.BillingPlansResponse;
import de.fiscalnorth.billing.dto.CheckoutSessionRequest;
import de.fiscalnorth.billing.dto.SessionUrlResponse;
import de.fiscalnorth.billing.dto.SubscriptionStatusResponse;
import de.fiscalnorth.billing.dto.SubscriptionSummaryDto;
import de.fiscalnorth.billing.config.StripeProperties;
import de.fiscalnorth.billing.service.EntitlementService;
import de.fiscalnorth.billing.service.StripeService;
import de.fiscalnorth.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final CurrentUserService currentUserService;
    private final EntitlementService entitlementService;
    private final StripeService stripeService;
    private final StripeProperties stripeProperties;

    @GetMapping("/subscription")
    public ResponseEntity<SubscriptionStatusResponse> getSubscription() {
        User user = currentUserService.getCurrentUser();
        SubscriptionSummaryDto summary = entitlementService.toSummary(user);
        return ResponseEntity.ok(new SubscriptionStatusResponse(
                summary.plan(),
                summary.status(),
                summary.entitlements(),
                summary.currentPeriodEnd(),
                summary.trialEnd(),
                summary.cancelAtPeriodEnd(),
                summary.premiumActive(),
                stripeProperties.isEnabled()));
    }

    @GetMapping("/plans")
    public ResponseEntity<BillingPlansResponse> getPlans() {
        List<BillingPlanDto> plans = new ArrayList<>();
        if (stripeProperties.isEnabled()) {
            if (stripeProperties.getPriceIdMonthly() != null && !stripeProperties.getPriceIdMonthly().isBlank()) {
                plans.add(new BillingPlanDto(
                        "premium-monthly",
                        "Premium",
                        stripeProperties.getPriceIdMonthly(),
                        "month",
                        stripeProperties.getTrialDays()));
            }
            if (stripeProperties.getPriceIdYearly() != null && !stripeProperties.getPriceIdYearly().isBlank()) {
                plans.add(new BillingPlanDto(
                        "premium-yearly",
                        "Premium",
                        stripeProperties.getPriceIdYearly(),
                        "year",
                        stripeProperties.getTrialDays()));
            }
        }
        return ResponseEntity.ok(new BillingPlansResponse(plans, stripeProperties.isEnabled()));
    }

    @PostMapping("/checkout-session")
    public ResponseEntity<SessionUrlResponse> createCheckoutSession(
            @Valid @RequestBody CheckoutSessionRequest request) throws StripeException {
        User user = currentUserService.getCurrentUser();
        var session = stripeService.createCheckoutSession(user, request.priceId());
        return ResponseEntity.ok(new SessionUrlResponse(session.getUrl()));
    }

    @PostMapping("/portal-session")
    public ResponseEntity<SessionUrlResponse> createPortalSession() throws StripeException {
        User user = currentUserService.getCurrentUser();
        var session = stripeService.createPortalSession(user);
        return ResponseEntity.ok(new SessionUrlResponse(session.getUrl()));
    }
}
