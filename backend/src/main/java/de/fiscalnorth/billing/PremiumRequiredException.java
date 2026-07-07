package de.fiscalnorth.billing;

import de.fiscalnorth.billing.model.PremiumFeature;
import de.fiscalnorth.shared.LocalizedException;
import lombok.Getter;

@Getter
public class PremiumRequiredException extends LocalizedException {

    private final PremiumFeature feature;

    public PremiumRequiredException(PremiumFeature feature) {
        super("error.premium.required", feature.name());
        this.feature = feature;
    }
}
