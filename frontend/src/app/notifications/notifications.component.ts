import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import {
    AppNotification,
    NotificationService,
    NotificationSeverity,
} from '../core/services/notification.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';
import { LanguageService } from '../core/i18n/language.service';

@Component({
    selector: 'app-notifications',
    standalone: true,
    imports: [CommonModule, RouterLink, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
    templateUrl: './notifications.component.html',
    styleUrl: './notifications.component.css',
})
export class NotificationsComponent implements OnInit {
    private readonly lang = inject(LanguageService);

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
                return this.lang.instant('notifications.typeBudget');
            case 'SPENDING_INSIGHT':
                return this.lang.instant('notifications.typeInsight');
            case 'OPTIMIZATION_TIP':
                return this.lang.instant('notifications.typeOptimization');
            default:
                return this.lang.instant('notifications.typeOther');
        }
    }

    severityClass(severity: NotificationSeverity): string {
        return severity.toLowerCase();
    }

    formatDate(iso: string): string {
        const d = new Date(iso);
        if (Number.isNaN(d.getTime())) return iso;
        return d.toLocaleString(this.lang.intlLocale(), {
            day: 'numeric',
            month: 'short',
            hour: '2-digit',
            minute: '2-digit',
        });
    }
}
