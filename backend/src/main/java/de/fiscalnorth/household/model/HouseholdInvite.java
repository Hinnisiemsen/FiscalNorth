package de.fiscalnorth.household.model;

import de.fiscalnorth.shared.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
public class HouseholdInvite extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    private String email;

    private String token;

    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    private HouseholdInviteStatus status = HouseholdInviteStatus.PENDING;
}
