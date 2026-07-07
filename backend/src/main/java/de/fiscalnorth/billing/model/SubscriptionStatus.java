package de.fiscalnorth.billing.model;

public enum SubscriptionStatus {
    ACTIVE,
    TRIALING,
    PAST_DUE,
    CANCELED,
    UNPAID,
    INCOMPLETE,
    INCOMPLETE_EXPIRED,
    NONE
}
