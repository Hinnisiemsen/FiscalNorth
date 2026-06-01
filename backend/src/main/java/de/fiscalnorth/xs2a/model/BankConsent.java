package de.fiscalnorth.xs2a.model;

import de.fiscalnorth.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Stored consent for XS2A bank account access.
 * One consent = one bank connection for a PSU (user).
 */
@Getter
@Setter
@Entity
public class BankConsent extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String consentId;

    @Column(nullable = false)
    private String psuId;

    @Enumerated(EnumType.STRING)
    private ConsentStatus status = ConsentStatus.PENDING;

    private LocalDate validUntil;

    public enum ConsentStatus {
        PENDING,
        VALID,
        EXPIRED,
        DELETED
    }
}
