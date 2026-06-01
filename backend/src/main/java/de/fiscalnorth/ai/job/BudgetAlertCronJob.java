package de.fiscalnorth.ai.job;

import de.fiscalnorth.ai.config.AiCronProperties;
import de.fiscalnorth.budget.dto.BudgetWithUsage;
import de.fiscalnorth.budget.service.BudgetService;
import de.fiscalnorth.notification.model.NotificationSeverity;
import de.fiscalnorth.notification.model.NotificationType;
import de.fiscalnorth.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class BudgetAlertCronJob {

    private static final Logger log = LoggerFactory.getLogger(BudgetAlertCronJob.class);
    private static final BigDecimal WARNING_THRESHOLD = new BigDecimal("0.80");
    private static final BigDecimal CRITICAL_THRESHOLD = new BigDecimal("1.00");

    private final AiCronProperties cronProperties;
    private final BudgetService budgetService;
    private final NotificationService notificationService;

    @Scheduled(cron = "${app.ai.cron.budget-alerts:0 0 8,20 * * *}")
    public void scanBudgets() {
        if (!cronProperties.enabled()) {
            return;
        }
        LocalDate today = LocalDate.now();
        int created = 0;
        for (BudgetWithUsage budget : budgetService.getBudgetsWithUsage()) {
            if (budget.endDate().isBefore(today) || budget.startDate().isAfter(today)) {
                continue;
            }
            if (budget.limit() == null || budget.limit().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal ratio = budget.spent().divide(budget.limit(), 4, RoundingMode.HALF_UP);
            if (ratio.compareTo(WARNING_THRESHOLD) < 0) {
                continue;
            }

            boolean critical = ratio.compareTo(CRITICAL_THRESHOLD) >= 0;
            String level = critical ? "critical" : "warning";
            String dedupeKey = "budget-alert:" + budget.id() + ":" + level + ":" + today;

            int pct = ratio.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).intValue();
            String title = critical ? "Budget überschritten" : "Budget fast aufgebraucht";
            String message = critical
                    ? "Das Budget „%s“ ist bei %d %% (%s von %s €). Bitte Ausgaben prüfen."
                            .formatted(budget.name(), pct, budget.spent(), budget.limit())
                    : "Das Budget „%s“ ist zu %d %% verbraucht (%s von %s €)."
                            .formatted(budget.name(), pct, budget.spent(), budget.limit());

            var saved = notificationService.createIfAbsent(
                    dedupeKey,
                    title,
                    message,
                    NotificationType.BUDGET_ALERT,
                    critical ? NotificationSeverity.CRITICAL : NotificationSeverity.WARNING,
                    "budget-alert-cron");
            if (saved.isPresent()) {
                created++;
            }
        }
        if (created > 0) {
            log.info("Budget alert cron created {} notification(s)", created);
        }
    }
}
