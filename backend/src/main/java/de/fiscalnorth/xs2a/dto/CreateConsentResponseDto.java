package de.fiscalnorth.xs2a.dto;

/**
 * Response after initiating bank connection - contains redirect URL for SCA.
 */
public record CreateConsentResponseDto(
        String consentId,
        String redirectUrl,
        String message
) {
}
