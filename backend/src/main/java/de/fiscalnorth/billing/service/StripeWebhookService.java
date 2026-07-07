package de.fiscalnorth.billing.service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import de.fiscalnorth.billing.config.StripeProperties;
import de.fiscalnorth.billing.model.ProcessedStripeEvent;
import de.fiscalnorth.billing.repository.ProcessedStripeEventRepository;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StripeWebhookService {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookService.class);

    private final StripeProperties stripeProperties;
    private final ProcessedStripeEventRepository processedStripeEventRepository;
    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

    @Transactional
    public void handleWebhook(String payload, String signatureHeader) {
        if (!stripeProperties.isConfigured()) {
            throw new IllegalStateException("Stripe webhooks are not configured");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, signatureHeader, stripeProperties.getWebhookSecret());
        } catch (SignatureVerificationException ex) {
            throw new IllegalArgumentException("Invalid Stripe webhook signature", ex);
        }

        if (processedStripeEventRepository.existsById(event.getId())) {
            log.debug("Skipping duplicate Stripe event {}", event.getId());
            return;
        }

        switch (event.getType()) {
            case "checkout.session.completed" -> handleCheckoutCompleted(event);
            case "customer.subscription.created", "customer.subscription.updated" ->
                    handleSubscriptionUpdated(event);
            case "customer.subscription.deleted" -> handleSubscriptionDeleted(event);
            case "invoice.payment_failed" -> handlePaymentFailed(event);
            default -> log.debug("Unhandled Stripe event type: {}", event.getType());
        }

        ProcessedStripeEvent processed = new ProcessedStripeEvent();
        processed.setEventId(event.getId());
        processed.setEventType(event.getType());
        processed.setProcessedAt(Instant.now());
        processedStripeEventRepository.save(processed);
    }

    private void handleCheckoutCompleted(Event event) {
        Session session = deserialize(event, Session.class);
        if (session == null) {
            return;
        }

        User user = resolveUser(session.getClientReferenceId(), session.getCustomer());
        if (user == null) {
            log.warn("checkout.session.completed without resolvable user: {}", session.getId());
            return;
        }

        if (user.getStripeCustomerId() == null && session.getCustomer() != null) {
            user.setStripeCustomerId(session.getCustomer());
            userRepository.save(user);
        }

        if (session.getSubscription() != null) {
            try {
                Subscription subscription = Subscription.retrieve(session.getSubscription());
                subscriptionService.upsertFromStripeSubscription(user, subscription);
            } catch (Exception ex) {
                log.error("Failed to retrieve subscription {} after checkout", session.getSubscription(), ex);
            }
        }
    }

    private void handleSubscriptionUpdated(Event event) {
        Subscription subscription = deserialize(event, Subscription.class);
        if (subscription == null) {
            return;
        }

        User user = resolveUserFromSubscription(subscription);
        if (user == null) {
            log.warn("Subscription event without resolvable user: {}", subscription.getId());
            return;
        }

        subscriptionService.upsertFromStripeSubscription(user, subscription);
    }

    private void handleSubscriptionDeleted(Event event) {
        Subscription subscription = deserialize(event, Subscription.class);
        if (subscription == null) {
            return;
        }

        subscriptionService.findByStripeSubscriptionId(subscription.getId())
                .ifPresent(existing -> subscriptionService.markCanceled(existing.getUser()));
    }

    private void handlePaymentFailed(Event event) {
        com.stripe.model.Invoice invoice = deserialize(event, com.stripe.model.Invoice.class);
        if (invoice == null || invoice.getSubscription() == null) {
            return;
        }
        try {
            Subscription subscription = Subscription.retrieve(invoice.getSubscription());
            User user = resolveUserFromSubscription(subscription);
            if (user != null) {
                subscriptionService.upsertFromStripeSubscription(user, subscription);
            }
        } catch (Exception ex) {
            log.error("Failed to process invoice.payment_failed for invoice {}", invoice.getId(), ex);
        }
    }

    private User resolveUserFromSubscription(Subscription subscription) {
        String userId = subscription.getMetadata() != null ? subscription.getMetadata().get("userId") : null;
        User user = resolveUser(userId, subscription.getCustomer());
        if (user != null) {
            return user;
        }
        return subscriptionService.findByStripeSubscriptionId(subscription.getId())
                .map(existing -> existing.getUser())
                .orElse(null);
    }

    private User resolveUser(String userId, String stripeCustomerId) {
        if (userId != null && !userId.isBlank()) {
            try {
                Optional<User> byId = userRepository.findById(Long.parseLong(userId));
                if (byId.isPresent()) {
                    return byId.get();
                }
            } catch (NumberFormatException ignored) {
                // fall through
            }
        }
        if (stripeCustomerId != null && !stripeCustomerId.isBlank()) {
            return userRepository.findByStripeCustomerId(stripeCustomerId).orElse(null);
        }
        return null;
    }

    private <T extends StripeObject> T deserialize(Event event, Class<T> type) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        if (deserializer.getObject().isEmpty()) {
            log.warn("Could not deserialize Stripe event {} payload", event.getId());
            return null;
        }
        StripeObject stripeObject = deserializer.getObject().get();
        if (!type.isInstance(stripeObject)) {
            log.warn("Unexpected Stripe object type for event {}: {}", event.getId(), stripeObject.getClass());
            return null;
        }
        return type.cast(stripeObject);
    }
}
