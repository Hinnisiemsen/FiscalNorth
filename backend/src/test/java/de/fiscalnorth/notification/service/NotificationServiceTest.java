package de.fiscalnorth.notification.service;

import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.notification.model.NotificationSeverity;
import de.fiscalnorth.notification.model.NotificationType;
import de.fiscalnorth.notification.repository.FinancialNotificationRepository;
import de.fiscalnorth.user.model.AuthProvider;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.model.UserRole;
import de.fiscalnorth.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest(properties = {
    "spring.sql.init.mode=never",
    "spring.jpa.defer-datasource-initialization=false"
})
@ActiveProfiles("test")
@Import({NotificationService.class, NotificationServiceTest.TestConfig.class})
class NotificationServiceTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        CurrentUserService currentUserService() {
            return mock(CurrentUserService.class);
        }
    }

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FinancialNotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    private Long ownerId;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUserName("Test");
        user.setEmail("test@example.com");
        user.setUserRole(UserRole.User);
        user.setAuthProvider(AuthProvider.LOCAL);
        ownerId = userRepository.save(user).getId();
    }

    @Test
    void createIfAbsentIsIdempotentForSameDedupeKey() {
        var first = notificationService.createIfAbsent(
                ownerId,
                "test:1",
                "Titel",
                "Nachricht mit genügend Inhalt.",
                NotificationType.SYSTEM,
                NotificationSeverity.INFO,
                "test");
        var second = notificationService.createIfAbsent(
                ownerId,
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
