package de.fiscalnorth.contract.model;

import de.fiscalnorth.shared.BaseEntity;
import jakarta.persistence.Entity;
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
