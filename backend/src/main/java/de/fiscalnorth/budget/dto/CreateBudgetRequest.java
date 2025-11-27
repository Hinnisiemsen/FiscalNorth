package de.fiscalnorth.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBudgetRequest(
        String name,
        BigDecimal limit,
        LocalDate startDate,
        LocalDate endDate) {
}
