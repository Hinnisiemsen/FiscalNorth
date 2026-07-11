package de.fiscalnorth.transaction.service;

import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.transaction.dto.InsightsResponse;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
import de.fiscalnorth.transaction.repository.TransactionSplitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InsightsServiceTest {

    private static final Long OWNER_ID = 42L;

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private TransactionSplitRepository transactionSplitRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private InsightsService insightsService;

    @BeforeEach
    void setUp() {
        when(currentUserService.getCurrentUserId()).thenReturn(OWNER_ID);
    }

    @Test
    void getInsights_mapsRepositoryRows() {
        LocalDate start = LocalDate.of(2026, 6, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        when(paymentTransactionRepository.sumExpensesByCategoryBetweenExcludingSplitParents(
                        eq(OWNER_ID), eq(start), eq(end)))
                .thenReturn(List.<Object[]>of(new Object[]{"Groceries", new BigDecimal("100.00")}));
        when(transactionSplitRepository.sumExpensesByCategoryBetween(eq(OWNER_ID), eq(start), eq(end)))
                .thenReturn(List.<Object[]>of(new Object[]{"Groceries", new BigDecimal("20.50")}));
        when(paymentTransactionRepository.sumByMonthAndTypeBetween(eq(OWNER_ID), eq(start), eq(end)))
                .thenReturn(List.<Object[]>of(new Object[]{2026, 6, "Expense", new BigDecimal("200.00")}));

        InsightsResponse response = insightsService.getInsights(2026, 6);

        assertThat(response.periodStart()).isEqualTo("2026-06-01");
        assertThat(response.periodEnd()).isEqualTo("2026-06-30");
        assertThat(response.spendingByCategory()).hasSize(1);
        assertThat(response.spendingByCategory().get(0).categoryName()).isEqualTo("Groceries");
        assertThat(response.spendingByCategory().get(0).amount()).isEqualByComparingTo("120.50");
        assertThat(response.monthlyTrends()).hasSize(1);
        assertThat(response.monthlyTrends().get(0).transactionType()).isEqualTo("Expense");
    }
}
