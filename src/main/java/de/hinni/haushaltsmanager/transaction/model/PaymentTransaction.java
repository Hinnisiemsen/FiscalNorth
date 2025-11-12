package de.hinni.haushaltsmanager.transaction.model;

import de.hinni.haushaltsmanager.category.model.Category;
import de.hinni.haushaltsmanager.contract.model.Contract;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PaymentTransaction extends Transaction {
    private String tags;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;
}
