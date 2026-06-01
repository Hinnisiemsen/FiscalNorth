package de.fiscalnorth.notification.model;

import de.fiscalnorth.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "financial_notification")
public class FinancialNotification extends BaseEntity {

    private String title;

    @Column(length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationSeverity severity;

    private boolean read;

    @Column(name = "dedupe_key", unique = true)
    private String dedupeKey;

    private String sourceJob;
}
