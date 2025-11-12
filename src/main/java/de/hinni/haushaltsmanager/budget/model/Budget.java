package de.hinni.haushaltsmanager.budget.model;

import de.hinni.haushaltsmanager.shared.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Budget extends BaseEntity {
    private String name;
    private BigDecimal limit;
    private LocalDate startDate;
    private LocalDate endDate;
}
