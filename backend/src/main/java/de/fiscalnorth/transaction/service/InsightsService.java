package de.fiscalnorth.transaction.service;

import de.fiscalnorth.household.service.HouseholdScopeService;
import de.fiscalnorth.transaction.dto.CategorySpendingDto;
import de.fiscalnorth.transaction.dto.InsightsResponse;
import de.fiscalnorth.transaction.dto.MonthlyTrendDto;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
import de.fiscalnorth.transaction.repository.TransactionSplitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InsightsService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final TransactionSplitRepository transactionSplitRepository;
    private final HouseholdScopeService householdScopeService;

    public InsightsResponse getInsights(int year, int month) {
        Long householdId = householdScopeService.requireHouseholdId();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return buildInsights(householdId, start, end);
    }

    public InsightsResponse getInsightsForYear(int year) {
        Long householdId = householdScopeService.requireHouseholdId();
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        return buildInsights(householdId, start, end);
    }

    public InsightsResponse getInsightsForOwner(Long ownerId, int year, int month) {
        Long householdId = householdScopeService.requireHouseholdId();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return buildInsights(householdId, start, end);
    }

    private InsightsResponse buildInsights(Long householdId, LocalDate start, LocalDate end) {
        Map<String, BigDecimal> categoryTotals = new HashMap<>();

        for (Object[] row : paymentTransactionRepository.sumHouseholdExpensesByCategoryBetweenExcludingSplitParents(
                householdId, start, end)) {
            addCategoryAmount(categoryTotals, row);
        }
        for (Object[] row : transactionSplitRepository.sumHouseholdExpensesByCategoryBetween(householdId, start, end)) {
            addCategoryAmount(categoryTotals, row);
        }

        List<CategorySpendingDto> byCategory = categoryTotals.entrySet().stream()
                .map(entry -> new CategorySpendingDto(entry.getKey(), entry.getValue()))
                .toList();

        List<MonthlyTrendDto> trends = new ArrayList<>();
        for (Object[] row : paymentTransactionRepository.sumHouseholdByMonthAndTypeBetween(householdId, start, end)) {
            int yr = toInt(row[0]);
            int mo = toInt(row[1]);
            String type = row[2] != null ? row[2].toString() : "";
            BigDecimal amount = row[3] != null ? (BigDecimal) row[3] : BigDecimal.ZERO;
            trends.add(new MonthlyTrendDto(yr, mo, type, amount));
        }

        return new InsightsResponse(byCategory, trends, start.toString(), end.toString());
    }

    private void addCategoryAmount(Map<String, BigDecimal> categoryTotals, Object[] row) {
        String categoryName = row[0] != null ? row[0].toString() : "Uncategorized";
        BigDecimal amount = row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO;
        categoryTotals.merge(categoryName, amount, BigDecimal::add);
    }

    private static int toInt(Object o) {
        if (o instanceof Number n) return n.intValue();
        if (o == null) return 0;
        return (int) Double.parseDouble(o.toString());
    }
}
