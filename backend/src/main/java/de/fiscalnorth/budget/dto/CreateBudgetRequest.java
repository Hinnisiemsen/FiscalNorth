package de.fiscalnorth.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateBudgetRequest(
                @NotBlank String name,
                @NotNull @Positive BigDecimal limit,
                @NotNull LocalDate startDate,
                @NotNull LocalDate endDate) {
}
