package de.fiscalnorth.transaction.dto;

import java.util.List;

public record InsightsResponse(
        List<CategorySpendingDto> spendingByCategory,
        List<MonthlyTrendDto> monthlyTrends,
        String periodStart,
        String periodEnd) {
}
