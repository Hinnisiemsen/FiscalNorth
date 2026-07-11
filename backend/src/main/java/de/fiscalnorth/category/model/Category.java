package de.fiscalnorth.category.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.fiscalnorth.household.model.Household;
import de.fiscalnorth.shared.BaseEntity;
import de.fiscalnorth.transaction.model.TransactionType;
import de.fiscalnorth.user.model.User;
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
@JsonIgnoreProperties({"owner", "household", "hibernateLazyInitializer", "handler"})
public class Category extends BaseEntity {
    private String name;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id")
    private Household household;
}
