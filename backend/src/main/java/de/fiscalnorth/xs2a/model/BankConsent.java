package de.fiscalnorth.xs2a.model;

import de.fiscalnorth.shared.BaseEntity;
import de.fiscalnorth.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class BankConsent extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String consentId;

    @Enumerated(EnumType.STRING)
    private ConsentStatus status = ConsentStatus.PENDING;

    private LocalDate validUntil;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public enum ConsentStatus {
        PENDING,
        VALID,
        EXPIRED,
        DELETED
    }
}
