package de.hinni.haushaltsmanager.transaction.model;

import de.hinni.haushaltsmanager.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Transaction extends BaseEntity {
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
}
