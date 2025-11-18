package de.fiscalnorth.transaction.model;

import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.contract.model.Contract;
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
