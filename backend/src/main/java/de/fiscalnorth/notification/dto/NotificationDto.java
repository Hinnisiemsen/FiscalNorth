package de.fiscalnorth.notification.dto;

import de.fiscalnorth.notification.model.NotificationSeverity;
import de.fiscalnorth.notification.model.NotificationType;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        String title,
        String message,
        NotificationType type,
        NotificationSeverity severity,
        boolean read,
        String sourceJob,
        LocalDateTime createdAt
) {
}
