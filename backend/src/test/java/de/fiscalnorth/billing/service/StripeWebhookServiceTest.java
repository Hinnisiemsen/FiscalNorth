package de.fiscalnorth.billing.service;

import de.fiscalnorth.billing.config.StripeProperties;
import de.fiscalnorth.billing.model.SubscriptionPlan;
import de.fiscalnorth.billing.model.SubscriptionStatus;
import de.fiscalnorth.billing.model.UserSubscription;
import de.fiscalnorth.billing.repository.UserSubscriptionRepository;
import de.fiscalnorth.user.model.AuthProvider;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StripeWebhookServiceTest {

    @Mock
    private UserSubscriptionRepository userSubscriptionRepository;

    @Test
    void mapStripeStatus_mapsKnownValues() {
        assertThat(SubscriptionService.mapStripeStatus("active")).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(SubscriptionService.mapStripeStatus("trialing")).isEqualTo(SubscriptionStatus.TRIALING);
        assertThat(SubscriptionService.mapStripeStatus("past_due")).isEqualTo(SubscriptionStatus.PAST_DUE);
        assertThat(SubscriptionService.mapStripeStatus("canceled")).isEqualTo(SubscriptionStatus.CANCELED);
    }

    @Test
    void markCanceled_resetsSubscriptionToFree() {
        User user = new User();
        user.setId(10L);
        user.setEmail("test@example.com");
        user.setUserName("Test");
        user.setUserRole(UserRole.User);
        user.setAuthProvider(AuthProvider.LOCAL);

        UserSubscription subscription = new UserSubscription();
        subscription.setUser(user);
        subscription.setPlan(SubscriptionPlan.PREMIUM);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStripeSubscriptionId("sub_123");

        when(userSubscriptionRepository.findByUserId(10L)).thenReturn(Optional.of(subscription));
        when(userSubscriptionRepository.save(any(UserSubscription.class))).thenAnswer(inv -> inv.getArgument(0));

        SubscriptionService subscriptionService = new SubscriptionService(
                userSubscriptionRepository, new StripeProperties());
        UserSubscription result = subscriptionService.markCanceled(user);

        assertThat(result.getPlan()).isEqualTo(SubscriptionPlan.FREE);
        assertThat(result.getStatus()).isEqualTo(SubscriptionStatus.CANCELED);
        assertThat(result.getStripeSubscriptionId()).isNull();
    }
}
