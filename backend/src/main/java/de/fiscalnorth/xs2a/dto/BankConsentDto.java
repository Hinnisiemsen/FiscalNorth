package de.fiscalnorth.xs2a.dto;

import de.fiscalnorth.xs2a.model.BankConsent;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BankConsentDto(
        Long id,
        String consentId,
        String psuId,
        String status,
        LocalDate validUntil,
        LocalDateTime createdAt
) {
    public static BankConsentDto from(BankConsent c) {
        return new BankConsentDto(
                c.getId(),
                c.getConsentId(),
                c.getOwner() != null ? c.getOwner().getId().toString() : null,
                c.getStatus().name(),
                c.getValidUntil(),
                c.getCreatedAt()
        );
    }
}
