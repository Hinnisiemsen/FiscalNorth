import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import {
    AppNotification,
    NotificationService,
    NotificationSeverity,
} from '../core/services/notification.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';

@Component({
    selector: 'app-notifications',
    standalone: true,
    imports: [CommonModule, RouterLink, ...PAGE_HEADER_IMPORTS],
    templateUrl: './notifications.component.html',
    styleUrl: './notifications.component.css',
})
export class NotificationsComponent implements OnInit {
    notifications: AppNotification[] = [];
    loading = true;
    filterUnread = false;

    constructor(private notificationService: NotificationService) {}

    ngOnInit(): void {
        this.load();
    }

    load(): void {
        this.loading = true;
        this.notificationService.list(this.filterUnread).subscribe({
            next: (items) => {
                this.notifications = items;
                this.loading = false;
            },
            error: () => {
                this.loading = false;
            },
        });
    }

    toggleFilter(): void {
        this.filterUnread = !this.filterUnread;
        this.load();
    }

    markRead(n: AppNotification): void {
        if (n.read) return;
        this.notificationService.markRead(n.id).subscribe({
            next: (updated) => {
                n.read = updated.read;
            },
        });
    }

    markAllRead(): void {
        this.notificationService.markAllRead().subscribe({
            next: () => {
                this.notifications.forEach((n) => (n.read = true));
            },
        });
    }

    typeLabel(type: AppNotification['type']): string {
        switch (type) {
            case 'BUDGET_ALERT':
                return 'Budget';
            case 'SPENDING_INSIGHT':
                return 'Ausgaben';
            case 'OPTIMIZATION_TIP':
                return 'Optimierung';
            default:
                return 'System';
        }
    }

    severityClass(severity: NotificationSeverity): string {
        return severity.toLowerCase();
    }

    formatDate(iso: string): string {
        const d = new Date(iso);
        if (Number.isNaN(d.getTime())) return iso;
        return d.toLocaleString('de-DE', {
            day: 'numeric',
            month: 'short',
            hour: '2-digit',
            minute: '2-digit',
        });
    }
}
