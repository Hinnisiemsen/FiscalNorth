package de.fiscalnorth.transaction.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.shared.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class TransactionSplit extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    @JsonBackReference
    private PaymentTransaction payment;

    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String note;
}
