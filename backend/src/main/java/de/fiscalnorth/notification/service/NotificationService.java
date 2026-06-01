package de.fiscalnorth.notification.service;

import de.fiscalnorth.notification.dto.NotificationDto;
import de.fiscalnorth.notification.model.FinancialNotification;
import de.fiscalnorth.notification.model.NotificationSeverity;
import de.fiscalnorth.notification.model.NotificationType;
import de.fiscalnorth.notification.repository.FinancialNotificationRepository;
import de.fiscalnorth.shared.RessourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FinancialNotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationDto> findAll() {
        return notificationRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> findUnread() {
        return notificationRepository.findByReadFalseOrderByCreatedAtDesc().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public long countUnread() {
        return notificationRepository.countByReadFalse();
    }

    @Transactional
    public NotificationDto markRead(Long id) {
        FinancialNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Notification", "id", id));
        notification.setRead(true);
        notification.setUpdatedAt(LocalDateTime.now());
        return toDto(notificationRepository.save(notification));
    }

    @Transactional
    public void markAllRead() {
        List<FinancialNotification> unread = notificationRepository.findByReadFalseOrderByCreatedAtDesc();
        LocalDateTime now = LocalDateTime.now();
        for (FinancialNotification notification : unread) {
            notification.setRead(true);
            notification.setUpdatedAt(now);
        }
        notificationRepository.saveAll(unread);
    }

    @Transactional
    public Optional<NotificationDto> createIfAbsent(
            String dedupeKey,
            String title,
            String message,
            NotificationType type,
            NotificationSeverity severity,
            String sourceJob
    ) {
        if (dedupeKey != null && notificationRepository.existsByDedupeKey(dedupeKey)) {
            return Optional.empty();
        }
        FinancialNotification notification = new FinancialNotification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setSeverity(severity);
        notification.setRead(false);
        notification.setDedupeKey(dedupeKey);
        notification.setSourceJob(sourceJob);
        LocalDateTime now = LocalDateTime.now();
        notification.setCreatedAt(now);
        notification.setUpdatedAt(now);
        return Optional.of(toDto(notificationRepository.save(notification)));
    }

    @Transactional
    public void pruneReadOlderThanDays(int days) {
        if (days <= 0) {
            return;
        }
        notificationRepository.deleteByReadTrueAndCreatedAtBefore(LocalDateTime.now().minusDays(days));
    }

    private NotificationDto toDto(FinancialNotification n) {
        return new NotificationDto(
                n.getId(),
                n.getTitle(),
                n.getMessage(),
                n.getType(),
                n.getSeverity(),
                n.isRead(),
                n.getSourceJob(),
                n.getCreatedAt());
    }
}
