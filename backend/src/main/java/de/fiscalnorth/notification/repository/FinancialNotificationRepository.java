package de.fiscalnorth.notification.repository;

import de.fiscalnorth.notification.model.FinancialNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FinancialNotificationRepository extends JpaRepository<FinancialNotification, Long> {

    List<FinancialNotification> findAllByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    List<FinancialNotification> findByOwnerIdAndReadFalseOrderByCreatedAtDesc(Long ownerId);

    long countByOwnerIdAndReadFalse(Long ownerId);

    boolean existsByOwnerIdAndDedupeKey(Long ownerId, String dedupeKey);

    Optional<FinancialNotification> findByIdAndOwnerId(Long id, Long ownerId);

    void deleteByOwnerIdAndReadTrueAndCreatedAtBefore(Long ownerId, LocalDateTime cutoff);
}
