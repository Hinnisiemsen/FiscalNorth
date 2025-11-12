package de.hinni.haushaltsmanager.account.model;

import de.hinni.haushaltsmanager.shared.BaseEntity;
import de.hinni.haushaltsmanager.shared.SupportedCurrency;
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
