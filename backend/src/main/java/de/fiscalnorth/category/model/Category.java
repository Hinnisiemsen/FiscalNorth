package de.fiscalnorth.category.model;

import de.fiscalnorth.shared.BaseEntity;
import de.fiscalnorth.transaction.model.TransactionType;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Category extends BaseEntity {
    private String name;
    private TransactionType transactionType;
}
