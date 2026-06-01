package de.fiscalnorth.category.model;

import de.fiscalnorth.shared.BaseEntity;
import de.fiscalnorth.transaction.model.TransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Category extends BaseEntity {
    private String name;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
}
