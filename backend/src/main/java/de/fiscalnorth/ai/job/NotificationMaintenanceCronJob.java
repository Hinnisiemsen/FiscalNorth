package de.fiscalnorth.ai.job;

import de.fiscalnorth.ai.config.AiCronProperties;
import de.fiscalnorth.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMaintenanceCronJob {

    private static final Logger log = LoggerFactory.getLogger(NotificationMaintenanceCronJob.class);

    private final AiCronProperties cronProperties;
    private final NotificationService notificationService;

    @Scheduled(cron = "${app.ai.cron.prune-read:0 0 3 * * SUN}")
    public void pruneOldReadNotifications() {
        if (!cronProperties.enabled()) {
            return;
        }
        notificationService.pruneReadOlderThanDays(cronProperties.pruneReadDays());
        log.debug("Pruned read notifications older than {} days", cronProperties.pruneReadDays());
    }
}
