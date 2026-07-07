package de.fiscalnorth.billing.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.billingportal.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import de.fiscalnorth.billing.BillingUnavailableException;
import de.fiscalnorth.billing.config.StripeProperties;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StripeService {

    private final StripeProperties stripeProperties;
    private final UserRepository userRepository;

    @PostConstruct
    void init() {
        if (stripeProperties.isEnabled() && stripeProperties.getSecretKey() != null) {
            Stripe.apiKey = stripeProperties.getSecretKey();
        }
    }

    public void requireBillingEnabled() {
        if (!stripeProperties.isEnabled()) {
            throw new BillingUnavailableException();
        }
    }

    @Transactional
    public String getOrCreateCustomer(User user) throws StripeException {
        requireBillingEnabled();
        if (user.getStripeCustomerId() != null && !user.getStripeCustomerId().isBlank()) {
            return user.getStripeCustomerId();
        }

        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(user.getEmail())
                .setName(user.getUserName())
                .putMetadata("userId", user.getId().toString())
                .build();
        Customer customer = Customer.create(params);
        user.setStripeCustomerId(customer.getId());
        userRepository.save(user);
        return customer.getId();
    }

    public com.stripe.model.checkout.Session createCheckoutSession(User user, String priceId)
            throws StripeException {
        requireBillingEnabled();
        validatePriceId(priceId);

        String customerId = getOrCreateCustomer(user);

        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setCustomer(customerId)
                .setClientReferenceId(user.getId().toString())
                .setSuccessUrl(stripeProperties.getCheckoutSuccessUrl())
                .setCancelUrl(stripeProperties.getCheckoutCancelUrl())
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(priceId)
                        .setQuantity(1L)
                        .build());

        if (stripeProperties.getTrialDays() > 0) {
            builder.setSubscriptionData(SessionCreateParams.SubscriptionData.builder()
                    .setTrialPeriodDays((long) stripeProperties.getTrialDays())
                    .putMetadata("userId", user.getId().toString())
                    .build());
        }

        return com.stripe.model.checkout.Session.create(builder.build());
    }

    public com.stripe.model.billingportal.Session createPortalSession(User user) throws StripeException {
        requireBillingEnabled();
        String customerId = getOrCreateCustomer(user);

        SessionCreateParams params = SessionCreateParams.builder()
                .setCustomer(customerId)
                .setReturnUrl(stripeProperties.getPortalReturnUrl())
                .build();
        return com.stripe.model.billingportal.Session.create(params);
    }

    private void validatePriceId(String priceId) {
        if (priceId == null || priceId.isBlank()) {
            throw new IllegalArgumentException("priceId is required");
        }
        if (!priceId.equals(stripeProperties.getPriceIdMonthly())
                && !priceId.equals(stripeProperties.getPriceIdYearly())) {
            throw new IllegalArgumentException("Unknown priceId");
        }
    }
}
