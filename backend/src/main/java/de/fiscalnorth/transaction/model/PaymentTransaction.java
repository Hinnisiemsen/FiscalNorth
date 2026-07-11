package de.fiscalnorth.transaction.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.contract.model.Contract;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<TransactionSplit> splits = new ArrayList<>();
}
