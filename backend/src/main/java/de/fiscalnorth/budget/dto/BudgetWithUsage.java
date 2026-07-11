package de.fiscalnorth.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BudgetWithUsage(
        Long id,
        String name,
        BigDecimal limit,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal spent,
        BigDecimal remaining,
        Long categoryId,
        String categoryName,
        List<MemberSpendingDto> memberBreakdown) {
}
