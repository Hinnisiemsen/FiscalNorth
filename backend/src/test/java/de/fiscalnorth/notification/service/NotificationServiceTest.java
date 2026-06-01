package de.fiscalnorth.notification.service;

import de.fiscalnorth.notification.model.NotificationSeverity;
import de.fiscalnorth.notification.model.NotificationType;
import de.fiscalnorth.notification.repository.FinancialNotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(NotificationService.class)
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FinancialNotificationRepository notificationRepository;

    @Test
    void createIfAbsentIsIdempotentForSameDedupeKey() {
        var first = notificationService.createIfAbsent(
                "test:1",
                "Titel",
                "Nachricht mit genügend Inhalt.",
                NotificationType.SYSTEM,
                NotificationSeverity.INFO,
                "test");
        var second = notificationService.createIfAbsent(
                "test:1",
                "Anders",
                "Andere Nachricht.",
                NotificationType.SYSTEM,
                NotificationSeverity.INFO,
                "test");

        assertThat(first).isPresent();
        assertThat(second).isEmpty();
        assertThat(notificationRepository.count()).isEqualTo(1);
    }
}
