package de.fiscalnorth.ai.job;

import de.fiscalnorth.ai.config.AiCronProperties;
import de.fiscalnorth.budget.dto.BudgetWithUsage;
import de.fiscalnorth.budget.service.BudgetService;
import de.fiscalnorth.notification.model.NotificationSeverity;
import de.fiscalnorth.notification.model.NotificationType;
import de.fiscalnorth.notification.service.NotificationService;
import de.fiscalnorth.shared.Messages;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.repository.UserRepository;
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
    private final Messages messages;
    private final UserRepository userRepository;

    @Scheduled(cron = "${app.ai.cron.budget-alerts:0 0 8,20 * * *}")
    public void scanBudgets() {
        if (!cronProperties.enabled()) {
            return;
        }
        var cronLocale = Messages.defaultCronLocale();
        LocalDate today = LocalDate.now();
        int created = 0;
        for (User user : userRepository.findAll()) {
            for (BudgetWithUsage budget : budgetService.getBudgetsWithUsageForOwner(user.getId())) {
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
                String title = critical
                        ? messages.getForLocale(cronLocale, "notification.budgetExceeded.title")
                        : messages.getForLocale(cronLocale, "notification.budgetWarning.title");
                String message = critical
                        ? messages.getForLocale(cronLocale, "notification.budgetExceeded.message",
                        budget.name(), pct, budget.spent(), budget.limit())
                        : messages.getForLocale(cronLocale, "notification.budgetWarning.message",
                        budget.name(), pct, budget.spent(), budget.limit());

                var saved = notificationService.createIfAbsent(
                        user.getId(),
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
        }
        if (created > 0) {
            log.info("Budget alert cron created {} notification(s)", created);
        }
    }
}
