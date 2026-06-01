package de.fiscalnorth.transaction.service;

import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.category.repository.CategoryRepository;
import de.fiscalnorth.contract.repository.ContractRepository;
import de.fiscalnorth.transaction.dto.CategorySpendingDto;
import de.fiscalnorth.transaction.dto.InsightsResponse;
import de.fiscalnorth.transaction.dto.MonthlyTrendDto;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InsightsService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final CurrentUserService currentUserService;

    public InsightsResponse getInsights(int year, int month) {
        Long ownerId = currentUserService.getCurrentUserId();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return buildInsights(ownerId, start, end);
    }

    public InsightsResponse getInsightsForYear(int year) {
        Long ownerId = currentUserService.getCurrentUserId();
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        return buildInsights(ownerId, start, end);
    }

    public InsightsResponse getInsightsForOwner(Long ownerId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return buildInsights(ownerId, start, end);
    }

    private InsightsResponse buildInsights(Long ownerId, LocalDate start, LocalDate end) {
        List<CategorySpendingDto> byCategory = new ArrayList<>();
        for (Object[] row : paymentTransactionRepository.sumExpensesByCategoryBetween(ownerId, start, end)) {
            String categoryName = row[0] != null ? row[0].toString() : "Uncategorized";
            BigDecimal amount = row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO;
            byCategory.add(new CategorySpendingDto(categoryName, amount));
        }

        List<MonthlyTrendDto> trends = new ArrayList<>();
        for (Object[] row : paymentTransactionRepository.sumByMonthAndTypeBetween(ownerId, start, end)) {
            int yr = toInt(row[0]);
            int mo = toInt(row[1]);
            String type = row[2] != null ? row[2].toString() : "";
            BigDecimal amount = row[3] != null ? (BigDecimal) row[3] : BigDecimal.ZERO;
            trends.add(new MonthlyTrendDto(yr, mo, type, amount));
        }

        return new InsightsResponse(byCategory, trends, start.toString(), end.toString());
    }

    private static int toInt(Object o) {
        if (o instanceof Number n) return n.intValue();
        if (o == null) return 0;
        return (int) Double.parseDouble(o.toString());
    }
}
