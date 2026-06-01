package de.fiscalnorth.notification.controller;

import de.fiscalnorth.ai.config.AiCronProperties;
import de.fiscalnorth.ai.job.BudgetAlertCronJob;
import de.fiscalnorth.ai.job.FinancialInsightsCronJob;
import de.fiscalnorth.ai.service.FinancialOptimizationService;
import de.fiscalnorth.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications/jobs")
@RequiredArgsConstructor
public class NotificationJobTriggerController {

    private final AiCronProperties cronProperties;
    private final BudgetAlertCronJob budgetAlertCronJob;
    private final FinancialInsightsCronJob financialInsightsCronJob;
    private final FinancialOptimizationService financialOptimizationService;
    private final NotificationService notificationService;

    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> runAll() {
        Map<String, Object> result = new LinkedHashMap<>();
        if (!cronProperties.enabled()) {
            result.put("enabled", false);
            return ResponseEntity.ok(result);
        }
        budgetAlertCronJob.scanBudgets();
        financialInsightsCronJob.publishInsights();
        int optimizationTips = financialOptimizationService.runOptimizationPass();
        result.put("enabled", true);
        result.put("optimizationTipsCreated", optimizationTips);
        result.put("unreadCount", notificationService.countUnread());
        return ResponseEntity.ok(result);
    }
}
