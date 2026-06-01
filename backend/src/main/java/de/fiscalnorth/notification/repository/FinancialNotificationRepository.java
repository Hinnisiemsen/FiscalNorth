package de.fiscalnorth.notification.repository;

import de.fiscalnorth.notification.model.FinancialNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FinancialNotificationRepository extends JpaRepository<FinancialNotification, Long> {

    List<FinancialNotification> findAllByOrderByCreatedAtDesc();

    List<FinancialNotification> findByReadFalseOrderByCreatedAtDesc();

    long countByReadFalse();

    boolean existsByDedupeKey(String dedupeKey);

    void deleteByReadTrueAndCreatedAtBefore(LocalDateTime cutoff);
}
