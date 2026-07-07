package de.fiscalnorth.billing.repository;

import de.fiscalnorth.billing.model.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    Optional<UserSubscription> findByUserId(Long userId);

    Optional<UserSubscription> findByStripeSubscriptionId(String stripeSubscriptionId);
}
