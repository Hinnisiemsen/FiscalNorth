package de.fiscalnorth.account.model;

import de.fiscalnorth.shared.BaseEntity;
import de.fiscalnorth.shared.SupportedCurrency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Account extends BaseEntity {
    protected String name;
    @Enumerated(EnumType.STRING)
    protected SupportedCurrency currency;
    protected BigDecimal balance;
}
