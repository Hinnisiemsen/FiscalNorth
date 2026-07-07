package de.fiscalnorth.billing.service;

import de.fiscalnorth.billing.config.StripeProperties;
import de.fiscalnorth.billing.model.PremiumFeature;
import de.fiscalnorth.billing.model.SubscriptionPlan;
import de.fiscalnorth.billing.model.SubscriptionStatus;
import de.fiscalnorth.billing.model.UserSubscription;
import de.fiscalnorth.billing.repository.UserSubscriptionRepository;
import de.fiscalnorth.user.model.AuthProvider;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntitlementServiceTest {

    @Mock
    private UserSubscriptionRepository userSubscriptionRepository;

    private EntitlementService entitlementService;
    private StripeProperties stripeProperties;

    @BeforeEach
    void setUp() {
        stripeProperties = new StripeProperties();
        stripeProperties.setPastDueGraceDays(3);
        SubscriptionService subscriptionService = new SubscriptionService(userSubscriptionRepository, stripeProperties);
        entitlementService = new EntitlementService(subscriptionService);
    }

    @Test
    void freeUser_hasNoPremiumFeatures() {
        User user = user(1L, UserRole.User);
        UserSubscription subscription = subscription(SubscriptionPlan.FREE, SubscriptionStatus.NONE);
        when(userSubscriptionRepository.findByUserId(1L)).thenReturn(Optional.of(subscription));

        assertThat(entitlementService.hasFeature(user, PremiumFeature.AI_ASSISTANT)).isFalse();
        assertThat(entitlementService.getEntitlements(user)).isEmpty();
    }

    @Test
    void activePremiumUser_hasAllFeatures() {
        User user = user(2L, UserRole.User);
        UserSubscription subscription = subscription(SubscriptionPlan.PREMIUM, SubscriptionStatus.ACTIVE);
        when(userSubscriptionRepository.findByUserId(2L)).thenReturn(Optional.of(subscription));

        assertThat(entitlementService.hasFeature(user, PremiumFeature.BANK_SYNC)).isTrue();
        assertThat(entitlementService.getEntitlements(user)).contains(PremiumFeature.AI_ASSISTANT);
    }

    @Test
    void trialingUser_hasPremiumAccess() {
        User user = user(3L, UserRole.User);
        UserSubscription subscription = subscription(SubscriptionPlan.PREMIUM, SubscriptionStatus.TRIALING);
        when(userSubscriptionRepository.findByUserId(3L)).thenReturn(Optional.of(subscription));

        assertThat(entitlementService.hasFeature(user, PremiumFeature.AI_GOAL_PLANNER)).isTrue();
    }

    @Test
    void pastDueWithinGrace_retainsPremiumAccess() {
        User user = user(4L, UserRole.User);
        UserSubscription subscription = subscription(SubscriptionPlan.PREMIUM, SubscriptionStatus.PAST_DUE);
        subscription.setPastDueSince(Instant.now().minusSeconds(3600));
        when(userSubscriptionRepository.findByUserId(4L)).thenReturn(Optional.of(subscription));

        assertThat(entitlementService.hasFeature(user, PremiumFeature.AI_NOTIFICATIONS)).isTrue();
    }

    @Test
    void pastDueAfterGrace_losesPremiumAccess() {
        User user = user(5L, UserRole.User);
        UserSubscription subscription = subscription(SubscriptionPlan.PREMIUM, SubscriptionStatus.PAST_DUE);
        subscription.setPastDueSince(Instant.now().minusSeconds(4 * 24 * 3600L));
        when(userSubscriptionRepository.findByUserId(5L)).thenReturn(Optional.of(subscription));

        assertThat(entitlementService.hasFeature(user, PremiumFeature.AI_ASSISTANT)).isFalse();
    }

    @Test
    void adminUser_bypassesSubscriptionChecks() {
        User admin = user(6L, UserRole.Admin);
        when(userSubscriptionRepository.findByUserId(6L)).thenReturn(Optional.empty());
        when(userSubscriptionRepository.save(any(UserSubscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(entitlementService.hasFeature(admin, PremiumFeature.BANK_SYNC)).isTrue();
    }

    private static User user(Long id, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setEmail("user" + id + "@example.com");
        user.setUserName("User " + id);
        user.setUserRole(role);
        user.setAuthProvider(AuthProvider.LOCAL);
        return user;
    }

    private static UserSubscription subscription(SubscriptionPlan plan, SubscriptionStatus status) {
        UserSubscription subscription = new UserSubscription();
        subscription.setPlan(plan);
        subscription.setStatus(status);
        return subscription;
    }
}
