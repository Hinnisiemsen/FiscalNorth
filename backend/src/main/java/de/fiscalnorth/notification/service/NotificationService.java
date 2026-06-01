package de.fiscalnorth.notification.service;

import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.notification.dto.NotificationDto;
import de.fiscalnorth.notification.model.FinancialNotification;
import de.fiscalnorth.notification.model.NotificationSeverity;
import de.fiscalnorth.notification.model.NotificationType;
import de.fiscalnorth.notification.repository.FinancialNotificationRepository;
import de.fiscalnorth.shared.RessourceNotFoundException;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.repository.UserRepository;
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
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<NotificationDto> findAll() {
        return notificationRepository.findAllByOwnerIdOrderByCreatedAtDesc(currentUserService.getCurrentUserId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> findUnread() {
        return notificationRepository.findByOwnerIdAndReadFalseOrderByCreatedAtDesc(currentUserService.getCurrentUserId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public long countUnread() {
        return notificationRepository.countByOwnerIdAndReadFalse(currentUserService.getCurrentUserId());
    }

    @Transactional
    public NotificationDto markRead(Long id) {
        FinancialNotification notification = notificationRepository
                .findByIdAndOwnerId(id, currentUserService.getCurrentUserId())
                .orElseThrow(() -> new RessourceNotFoundException("Notification", "id", id));
        notification.setRead(true);
        notification.setUpdatedAt(LocalDateTime.now());
        return toDto(notificationRepository.save(notification));
    }

    @Transactional
    public void markAllRead() {
        Long ownerId = currentUserService.getCurrentUserId();
        List<FinancialNotification> unread =
                notificationRepository.findByOwnerIdAndReadFalseOrderByCreatedAtDesc(ownerId);
        LocalDateTime now = LocalDateTime.now();
        for (FinancialNotification notification : unread) {
            notification.setRead(true);
            notification.setUpdatedAt(now);
        }
        notificationRepository.saveAll(unread);
    }

    @Transactional
    public Optional<NotificationDto> createIfAbsent(
            Long ownerId,
            String dedupeKey,
            String title,
            String message,
            NotificationType type,
            NotificationSeverity severity,
            String sourceJob
    ) {
        if (dedupeKey != null && notificationRepository.existsByOwnerIdAndDedupeKey(ownerId, dedupeKey)) {
            return Optional.empty();
        }
        User owner = userRepository.findById(ownerId).orElseThrow();
        FinancialNotification notification = new FinancialNotification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setSeverity(severity);
        notification.setRead(false);
        notification.setDedupeKey(dedupeKey);
        notification.setSourceJob(sourceJob);
        notification.setOwner(owner);
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
        for (User user : userRepository.findAll()) {
            notificationRepository.deleteByOwnerIdAndReadTrueAndCreatedAtBefore(
                    user.getId(), LocalDateTime.now().minusDays(days));
        }
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
