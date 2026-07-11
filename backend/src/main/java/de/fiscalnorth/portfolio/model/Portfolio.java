package de.fiscalnorth.portfolio.model;

import de.fiscalnorth.household.model.Household;
import de.fiscalnorth.shared.BaseEntity;
import de.fiscalnorth.shared.SupportedCurrency;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Portfolio extends BaseEntity {
    private String name;

    @Enumerated(EnumType.STRING)
    private SupportedCurrency baseCurrency = SupportedCurrency.EURO;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;
}
