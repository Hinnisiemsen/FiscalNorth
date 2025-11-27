package de.fiscalnorth.contract.dto;

import de.fiscalnorth.contract.model.ContractInterval;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateContractRequest(
        String name,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal amount,
        ContractInterval contractInterval,
        boolean autoDetected) {
}
