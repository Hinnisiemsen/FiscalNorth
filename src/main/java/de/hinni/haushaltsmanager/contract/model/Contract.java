package de.hinni.haushaltsmanager.contract.model;

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
public class Contract extends BaseEntity {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal amount;
    private ContractInterval contractInterval;
    private boolean autoDetected;
}
