package de.fiscalnorth.transaction.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.fiscalnorth.household.model.Household;
import de.fiscalnorth.shared.BaseEntity;
import de.fiscalnorth.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"owner_id", "import_hash"})
})
@JsonIgnoreProperties({"owner", "household", "hibernateLazyInitializer", "handler"})
public abstract class Transaction extends BaseEntity {
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    /** Hash for duplicate detection when re-importing (date|amount|description) */
    @Column(name = "import_hash")
    private String importHash;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id")
    private Household household;
}
