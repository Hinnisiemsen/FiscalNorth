package de.fiscalnorth.ai.job;

import de.fiscalnorth.ai.config.AiCronProperties;
import de.fiscalnorth.ai.service.FinancialOptimizationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinancialOptimizationCronJob {

    private static final Logger log = LoggerFactory.getLogger(FinancialOptimizationCronJob.class);

    private final AiCronProperties cronProperties;
    private final FinancialOptimizationService optimizationService;

    @Scheduled(cron = "${app.ai.cron.optimization:0 0 6 * * *}")
    public void generateOptimizationTips() {
        if (!cronProperties.enabled()) {
            return;
        }
        int created = optimizationService.runOptimizationPass();
        if (created > 0) {
            log.info("Optimization cron created {} AI tip notification(s)", created);
        }
    }
}
