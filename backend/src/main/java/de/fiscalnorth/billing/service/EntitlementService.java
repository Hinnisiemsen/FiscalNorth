package de.fiscalnorth.billing.service;

import de.fiscalnorth.billing.PremiumRequiredException;
import de.fiscalnorth.billing.dto.SubscriptionSummaryDto;
import de.fiscalnorth.billing.model.PremiumFeature;
import de.fiscalnorth.billing.model.SubscriptionPlan;
import de.fiscalnorth.billing.model.SubscriptionStatus;
import de.fiscalnorth.billing.model.UserSubscription;
import de.fiscalnorth.billing.config.StripeProperties;
import de.fiscalnorth.config.DemoProperties;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntitlementService {

    private static final Set<PremiumFeature> PREMIUM_FEATURES = EnumSet.allOf(PremiumFeature.class);

    private final SubscriptionService subscriptionService;
    private final DemoProperties demoProperties;
    private final StripeProperties stripeProperties;

    public boolean isPremiumPreviewEnabled() {
        return demoProperties.isPremiumPreviewEnabled();
    }

    public boolean hasFeature(User user, PremiumFeature feature) {
        if (demoProperties.isPremiumPreviewEnabled()) {
            return PREMIUM_FEATURES.contains(feature);
        }
        if (user.getUserRole() == UserRole.Admin) {
            return true;
        }
        if (!PREMIUM_FEATURES.contains(feature)) {
            return false;
        }
        return subscriptionService.findSubscription(user)
                .map(subscriptionService::isPremiumActive)
                .orElse(false);
    }

    public void requireFeature(User user, PremiumFeature feature) {
        if (!hasFeature(user, feature)) {
            throw new PremiumRequiredException(feature);
        }
    }

    public Set<PremiumFeature> getEntitlements(User user) {
        if (demoProperties.isPremiumPreviewEnabled()) {
            return EnumSet.copyOf(PREMIUM_FEATURES);
        }
        if (user.getUserRole() == UserRole.Admin) {
            return EnumSet.copyOf(PREMIUM_FEATURES);
        }
        return subscriptionService.findSubscription(user)
                .filter(subscriptionService::isPremiumActive)
                .map(subscription -> EnumSet.copyOf(PREMIUM_FEATURES))
                .orElseGet(() -> EnumSet.noneOf(PremiumFeature.class));
    }

    public boolean isPaidSubscriber(User user) {
        if (user.getUserRole() == UserRole.Admin) {
            return true;
        }
        return subscriptionService.findSubscription(user)
                .map(subscriptionService::isPremiumActive)
                .orElse(false);
    }

    public SubscriptionSummaryDto toSummary(User user) {
        boolean premiumActive = hasFeature(user, PremiumFeature.AI_ASSISTANT);
        boolean paidSubscriptionActive = isPaidSubscriber(user);
        boolean premiumPreviewEnabled = demoProperties.isPremiumPreviewEnabled();
        boolean billingEnabled = stripeProperties.isEnabled();
        SubscriptionPlan plan = premiumActive ? SubscriptionPlan.PREMIUM : SubscriptionPlan.FREE;
        List<String> entitlements = getEntitlements(user).stream()
                .map(PremiumFeature::name)
                .sorted()
                .collect(Collectors.toList());

        return subscriptionService.findSubscription(user)
                .map(subscription -> new SubscriptionSummaryDto(
                        plan,
                        subscription.getStatus(),
                        entitlements,
                        subscription.getCurrentPeriodEnd(),
                        subscription.getTrialEnd(),
                        subscription.isCancelAtPeriodEnd(),
                        premiumActive,
                        billingEnabled,
                        premiumPreviewEnabled,
                        paidSubscriptionActive))
                .orElseGet(() -> new SubscriptionSummaryDto(
                        SubscriptionPlan.FREE,
                        SubscriptionStatus.NONE,
                        entitlements,
                        null,
                        null,
                        false,
                        premiumActive,
                        billingEnabled,
                        premiumPreviewEnabled,
                        paidSubscriptionActive));
    }
}
