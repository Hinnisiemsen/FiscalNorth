package de.fiscalnorth.billing.service;

import com.stripe.model.Subscription;
import de.fiscalnorth.billing.config.StripeProperties;
import de.fiscalnorth.billing.model.SubscriptionPlan;
import de.fiscalnorth.billing.model.SubscriptionStatus;
import de.fiscalnorth.billing.model.UserSubscription;
import de.fiscalnorth.billing.repository.UserSubscriptionRepository;
import de.fiscalnorth.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final StripeProperties stripeProperties;

    public Optional<UserSubscription> findSubscription(User user) {
        return userSubscriptionRepository.findByUserId(user.getId());
    }

    public UserSubscription getOrCreateSubscription(User user) {
        return userSubscriptionRepository.findByUserId(user.getId())
                .orElseGet(() -> createFreeSubscription(user));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserSubscription createFreeSubscription(User user) {
        UserSubscription subscription = new UserSubscription();
        subscription.setUser(user);
        subscription.setPlan(SubscriptionPlan.FREE);
        subscription.setStatus(SubscriptionStatus.NONE);
        return userSubscriptionRepository.save(subscription);
    }

    @Transactional
    public UserSubscription upsertFromStripeSubscription(User user, Subscription stripeSubscription) {
        UserSubscription subscription = userSubscriptionRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserSubscription created = new UserSubscription();
                    created.setUser(user);
                    return created;
                });

        SubscriptionStatus status = mapStripeStatus(stripeSubscription.getStatus());
        subscription.setStripeSubscriptionId(stripeSubscription.getId());
        if (stripeSubscription.getItems() != null
                && stripeSubscription.getItems().getData() != null
                && !stripeSubscription.getItems().getData().isEmpty()) {
            subscription.setStripePriceId(stripeSubscription.getItems().getData().getFirst().getPrice().getId());
        }
        subscription.setStatus(status);
        subscription.setPlan(resolvePlan(status, subscription));
        subscription.setCurrentPeriodStart(toInstant(stripeSubscription.getCurrentPeriodStart()));
        subscription.setCurrentPeriodEnd(toInstant(stripeSubscription.getCurrentPeriodEnd()));
        subscription.setTrialEnd(toInstant(stripeSubscription.getTrialEnd()));
        subscription.setCancelAtPeriodEnd(Boolean.TRUE.equals(stripeSubscription.getCancelAtPeriodEnd()));

        if (status == SubscriptionStatus.PAST_DUE && subscription.getPastDueSince() == null) {
            subscription.setPastDueSince(Instant.now());
        } else if (status != SubscriptionStatus.PAST_DUE) {
            subscription.setPastDueSince(null);
        }

        return userSubscriptionRepository.save(subscription);
    }

    @Transactional
    public UserSubscription markCanceled(User user) {
        UserSubscription subscription = getOrCreateSubscription(user);
        subscription.setPlan(SubscriptionPlan.FREE);
        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscription.setStripeSubscriptionId(null);
        subscription.setStripePriceId(null);
        subscription.setCurrentPeriodStart(null);
        subscription.setCurrentPeriodEnd(null);
        subscription.setTrialEnd(null);
        subscription.setCancelAtPeriodEnd(false);
        subscription.setPastDueSince(null);
        return userSubscriptionRepository.save(subscription);
    }

    public Optional<UserSubscription> findByStripeSubscriptionId(String stripeSubscriptionId) {
        return userSubscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId);
    }

    public boolean isPremiumActive(UserSubscription subscription) {
        if (subscription.getPlan() != SubscriptionPlan.PREMIUM) {
            return false;
        }
        return switch (subscription.getStatus()) {
            case ACTIVE, TRIALING -> true;
            case PAST_DUE -> isWithinPastDueGrace(subscription);
            default -> false;
        };
    }

    private boolean isWithinPastDueGrace(UserSubscription subscription) {
        if (subscription.getPastDueSince() == null) {
            return true;
        }
        Instant graceEnd = subscription.getPastDueSince()
                .plusSeconds(stripeProperties.getPastDueGraceDays() * 24L * 60L * 60L);
        return Instant.now().isBefore(graceEnd);
    }

    private SubscriptionPlan resolvePlan(SubscriptionStatus status, UserSubscription subscription) {
        SubscriptionStatus effectiveStatus = status;
        if (status == SubscriptionStatus.PAST_DUE && !isWithinPastDueGrace(subscription)) {
            return SubscriptionPlan.FREE;
        }
        return switch (effectiveStatus) {
            case ACTIVE, TRIALING, PAST_DUE -> SubscriptionPlan.PREMIUM;
            default -> SubscriptionPlan.FREE;
        };
    }

    static SubscriptionStatus mapStripeStatus(String stripeStatus) {
        if (stripeStatus == null) {
            return SubscriptionStatus.NONE;
        }
        return switch (stripeStatus) {
            case "active" -> SubscriptionStatus.ACTIVE;
            case "trialing" -> SubscriptionStatus.TRIALING;
            case "past_due" -> SubscriptionStatus.PAST_DUE;
            case "canceled" -> SubscriptionStatus.CANCELED;
            case "unpaid" -> SubscriptionStatus.UNPAID;
            case "incomplete" -> SubscriptionStatus.INCOMPLETE;
            case "incomplete_expired" -> SubscriptionStatus.INCOMPLETE_EXPIRED;
            default -> SubscriptionStatus.NONE;
        };
    }

    private static Instant toInstant(Long epochSeconds) {
        return epochSeconds != null ? Instant.ofEpochSecond(epochSeconds) : null;
    }
}
