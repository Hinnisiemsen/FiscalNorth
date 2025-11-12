package de.hinni.haushaltsmanager.category.model;

import de.hinni.haushaltsmanager.shared.BaseEntity;
import de.hinni.haushaltsmanager.transaction.model.TransactionType;
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
