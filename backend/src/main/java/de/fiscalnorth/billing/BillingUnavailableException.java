package de.fiscalnorth.billing;

import de.fiscalnorth.shared.LocalizedException;

public class BillingUnavailableException extends LocalizedException {

    public BillingUnavailableException() {
        super("error.billing.unavailable");
    }
}
