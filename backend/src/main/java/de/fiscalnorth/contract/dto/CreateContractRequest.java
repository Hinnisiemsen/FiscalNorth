package de.fiscalnorth.contract.dto;

import de.fiscalnorth.contract.model.ContractInterval;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateContractRequest(
                @NotBlank String name,
                @NotNull LocalDate startDate,
                @NotNull LocalDate endDate,
                @NotNull @Positive BigDecimal amount,
                @NotNull ContractInterval contractInterval,
                boolean autoDetected) {
}
