package de.hinni.haushaltsmanager.account.model;

import de.hinni.haushaltsmanager.BaseEntity;
import de.hinni.haushaltsmanager.shared.SupportedCurrencies;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public abstract class Account extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private SupportedCurrencies curreny;
    private BigDecimal balance;
}
