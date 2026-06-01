package de.fiscalnorth.budget.model;

import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Budget extends BaseEntity {
    private String name;
    @Column(name = "budget_limit")
    private BigDecimal limit;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
