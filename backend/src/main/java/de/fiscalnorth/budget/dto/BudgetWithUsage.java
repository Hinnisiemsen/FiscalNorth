package de.fiscalnorth.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetWithUsage(
        Long id,
        String name,
        BigDecimal limit,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal spent,
        Long categoryId,
        String categoryName) {
}
