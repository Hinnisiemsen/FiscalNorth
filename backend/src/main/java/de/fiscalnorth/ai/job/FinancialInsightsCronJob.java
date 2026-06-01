package de.fiscalnorth.ai.job;

import de.fiscalnorth.account.model.DepositAccount;
import de.fiscalnorth.account.repository.DepositAccountRepository;
import de.fiscalnorth.ai.config.AiCronProperties;
import de.fiscalnorth.contract.model.Contract;
import de.fiscalnorth.contract.repository.ContractRepository;
import de.fiscalnorth.notification.model.NotificationSeverity;
import de.fiscalnorth.notification.model.NotificationType;
import de.fiscalnorth.notification.service.NotificationService;
import de.fiscalnorth.shared.Messages;
import de.fiscalnorth.transaction.dto.CategorySpendingDto;
import de.fiscalnorth.transaction.service.InsightsService;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FinancialInsightsCronJob {

    private static final Logger log = LoggerFactory.getLogger(FinancialInsightsCronJob.class);

    private final AiCronProperties cronProperties;
    private final DepositAccountRepository depositAccountRepository;
    private final ContractRepository contractRepository;
    private final InsightsService insightsService;
    private final NotificationService notificationService;
    private final Messages messages;
    private final UserRepository userRepository;

    @Scheduled(cron = "${app.ai.cron.insights:0 30 12 * * *}")
    public void publishInsights() {
        if (!cronProperties.enabled()) {
            return;
        }
        var cronLocale = Messages.defaultCronLocale();
        LocalDate today = LocalDate.now();
        int created = 0;

        for (User user : userRepository.findAll()) {
            Long ownerId = user.getId();
            BigDecimal totalBalance = depositAccountRepository.findAllByOwnerId(ownerId).stream()
                    .map(DepositAccount::getBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal monthlyFixed = contractRepository.findAllByOwnerId(ownerId).stream()
                    .map(Contract::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal disposable = totalBalance.subtract(monthlyFixed);

            if (monthlyFixed.compareTo(BigDecimal.ZERO) > 0 && disposable.compareTo(BigDecimal.ZERO) < 0) {
                String dedupeKey = "insight:negative-disposable:" + today;
                var saved = notificationService.createIfAbsent(
                        ownerId,
                        dedupeKey,
                        messages.getForLocale(cronLocale, "notification.negativeDisposable.title"),
                        messages.getForLocale(cronLocale, "notification.negativeDisposable.message", monthlyFixed, totalBalance),
                        NotificationType.SPENDING_INSIGHT,
                        NotificationSeverity.CRITICAL,
                        "insights-cron");
                if (saved.isPresent()) {
                    created++;
                }
            }

            var insights = insightsService.getInsightsForOwner(ownerId, today.getYear(), today.getMonthValue());
            List<CategorySpendingDto> top = insights.spendingByCategory().stream()
                    .sorted(Comparator.comparing(CategorySpendingDto::amount).reversed())
                    .limit(1)
                    .toList();
            if (!top.isEmpty() && top.get(0).amount().compareTo(BigDecimal.ZERO) > 0) {
                CategorySpendingDto leader = top.get(0);
                String dedupeKey = "insight:top-category:" + today.getYear() + "-" + today.getMonthValue();
                var saved = notificationService.createIfAbsent(
                        ownerId,
                        dedupeKey,
                        messages.getForLocale(cronLocale, "notification.topCategory.title"),
                        messages.getForLocale(cronLocale, "notification.topCategory.message",
                                leader.categoryName(), leader.amount()),
                        NotificationType.SPENDING_INSIGHT,
                        NotificationSeverity.INFO,
                        "insights-cron");
                if (saved.isPresent()) {
                    created++;
                }
            }
        }

        if (created > 0) {
            log.info("Insights cron created {} notification(s)", created);
        }
    }
}
