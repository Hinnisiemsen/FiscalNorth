package de.fiscalnorth.transaction.service;

import de.fiscalnorth.transaction.dto.CategorySpendingDto;
import de.fiscalnorth.transaction.dto.InsightsResponse;
import de.fiscalnorth.transaction.dto.MonthlyTrendDto;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InsightsService {

    private final PaymentTransactionRepository paymentTransactionRepository;

    public InsightsService(PaymentTransactionRepository paymentTransactionRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    public InsightsResponse getInsights(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<CategorySpendingDto> byCategory = new ArrayList<>();
        for (Object[] row : paymentTransactionRepository.sumExpensesByCategoryBetween(start, end)) {
            String categoryName = row[0] != null ? row[0].toString() : "Uncategorized";
            BigDecimal amount = row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO;
            byCategory.add(new CategorySpendingDto(categoryName, amount));
        }

        List<MonthlyTrendDto> trends = new ArrayList<>();
        for (Object[] row : paymentTransactionRepository.sumByMonthAndTypeBetween(start, end)) {
            int yr = toInt(row[0]);
            int mo = toInt(row[1]);
            String type = row[2] != null ? row[2].toString() : "";
            BigDecimal amount = row[3] != null ? (BigDecimal) row[3] : BigDecimal.ZERO;
            trends.add(new MonthlyTrendDto(yr, mo, type, amount));
        }

        return new InsightsResponse(byCategory, trends, start.toString(), end.toString());
    }

    public InsightsResponse getInsightsForYear(int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<CategorySpendingDto> byCategory = new ArrayList<>();
        for (Object[] row : paymentTransactionRepository.sumExpensesByCategoryBetween(start, end)) {
            String categoryName = row[0] != null ? row[0].toString() : "Uncategorized";
            BigDecimal amount = row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO;
            byCategory.add(new CategorySpendingDto(categoryName, amount));
        }

        List<MonthlyTrendDto> trends = new ArrayList<>();
        for (Object[] row : paymentTransactionRepository.sumByMonthAndTypeBetween(start, end)) {
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
