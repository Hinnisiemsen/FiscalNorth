import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import {
  BillingPlansResponse,
  SessionUrlResponse,
  SubscriptionStatusResponse,
} from '../models/billing.model';

@Injectable({ providedIn: 'root' })
export class BillingService {
  private readonly api = inject(ApiService);

  getSubscription(): Observable<SubscriptionStatusResponse> {
    return this.api.get<SubscriptionStatusResponse>('/billing/subscription');
  }

  getPlans(): Observable<BillingPlansResponse> {
    return this.api.get<BillingPlansResponse>('/billing/plans');
  }

  createCheckoutSession(priceId: string): Observable<SessionUrlResponse> {
    return this.api.post<SessionUrlResponse>('/billing/checkout-session', { priceId });
  }

  createPortalSession(): Observable<SessionUrlResponse> {
    return this.api.post<SessionUrlResponse>('/billing/portal-session', {});
  }
}
