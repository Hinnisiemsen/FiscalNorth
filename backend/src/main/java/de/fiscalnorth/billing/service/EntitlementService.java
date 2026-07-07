package de.fiscalnorth.billing.service;

import de.fiscalnorth.billing.PremiumRequiredException;
import de.fiscalnorth.billing.dto.SubscriptionSummaryDto;
import de.fiscalnorth.billing.model.PremiumFeature;
import de.fiscalnorth.billing.model.SubscriptionPlan;
import de.fiscalnorth.billing.model.UserSubscription;
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

    public boolean hasFeature(User user, PremiumFeature feature) {
        if (user.getUserRole() == UserRole.Admin) {
            return true;
        }
        if (!PREMIUM_FEATURES.contains(feature)) {
            return false;
        }
        UserSubscription subscription = subscriptionService.getOrCreateSubscription(user);
        return subscriptionService.isPremiumActive(subscription);
    }

    public void requireFeature(User user, PremiumFeature feature) {
        if (!hasFeature(user, feature)) {
            throw new PremiumRequiredException(feature);
        }
    }

    public Set<PremiumFeature> getEntitlements(User user) {
        if (user.getUserRole() == UserRole.Admin) {
            return EnumSet.copyOf(PREMIUM_FEATURES);
        }
        UserSubscription subscription = subscriptionService.getOrCreateSubscription(user);
        if (subscriptionService.isPremiumActive(subscription)) {
            return EnumSet.copyOf(PREMIUM_FEATURES);
        }
        return EnumSet.noneOf(PremiumFeature.class);
    }

    public SubscriptionSummaryDto toSummary(User user) {
        UserSubscription subscription = subscriptionService.getOrCreateSubscription(user);
        boolean premiumActive = hasFeature(user, PremiumFeature.AI_ASSISTANT);
        SubscriptionPlan plan = premiumActive ? SubscriptionPlan.PREMIUM : SubscriptionPlan.FREE;
        List<String> entitlements = getEntitlements(user).stream()
                .map(PremiumFeature::name)
                .sorted()
                .collect(Collectors.toList());
        return new SubscriptionSummaryDto(
                plan,
                subscription.getStatus(),
                entitlements,
                subscription.getCurrentPeriodEnd(),
                subscription.getTrialEnd(),
                subscription.isCancelAtPeriodEnd(),
                premiumActive);
    }
}
