package de.fiscalnorth.billing.dto;

import jakarta.validation.constraints.NotBlank;

public record CheckoutSessionRequest(
        @NotBlank String priceId
) {
}
