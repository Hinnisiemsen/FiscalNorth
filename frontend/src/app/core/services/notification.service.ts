import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export type NotificationType = 'BUDGET_ALERT' | 'SPENDING_INSIGHT' | 'OPTIMIZATION_TIP' | 'SYSTEM';
export type NotificationSeverity = 'INFO' | 'WARNING' | 'CRITICAL';

export interface AppNotification {
    id: number;
    title: string;
    message: string;
    type: NotificationType;
    severity: NotificationSeverity;
    read: boolean;
    sourceJob: string;
    createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
    constructor(private api: ApiService) {}

    list(unreadOnly = false): Observable<AppNotification[]> {
        const params = unreadOnly ? '?unreadOnly=true' : '';
        return this.api.get<AppNotification[]>(`/notifications${params}`);
    }

    unreadCount(): Observable<{ count: number }> {
        return this.api.get<{ count: number }>('/notifications/unread-count');
    }

    markRead(id: number): Observable<AppNotification> {
        return this.api.patch<AppNotification>(`/notifications/${id}/read`, {});
    }

    markAllRead(): Observable<void> {
        return this.api.patch<void>('/notifications/read-all', {});
    }
}
