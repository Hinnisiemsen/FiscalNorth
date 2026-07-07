import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { BillingService } from './billing.service';
import { PremiumFeature, SubscriptionSummary } from '../models/billing.model';
import { UserProfile } from './user.service';

const EMPTY_SUBSCRIPTION: SubscriptionSummary = {
  plan: 'FREE',
  status: 'NONE',
  entitlements: [],
  currentPeriodEnd: null,
  trialEnd: null,
  cancelAtPeriodEnd: false,
  premiumActive: false,
};

@Injectable({ providedIn: 'root' })
export class EntitlementService {
  private readonly billingService = inject(BillingService);

  private readonly subscriptionSubject = new BehaviorSubject<SubscriptionSummary>(
    EMPTY_SUBSCRIPTION,
  );
  readonly subscription$ = this.subscriptionSubject.asObservable();

  syncFromProfile(profile: UserProfile): void {
    if (profile.subscription) {
      this.subscriptionSubject.next(profile.subscription);
    }
  }

  refresh(): Observable<SubscriptionSummary> {
    return this.billingService.getSubscription().pipe(
      tap((response) => {
        this.subscriptionSubject.next({
          plan: response.plan,
          status: response.status,
          entitlements: response.entitlements,
          currentPeriodEnd: response.currentPeriodEnd,
          trialEnd: response.trialEnd,
          cancelAtPeriodEnd: response.cancelAtPeriodEnd,
          premiumActive: response.premiumActive,
        });
      }),
    );
  }

  hasFeature(feature: PremiumFeature): boolean {
    return this.subscriptionSubject.value.entitlements.includes(feature);
  }

  get isPremium(): boolean {
    return this.subscriptionSubject.value.premiumActive;
  }

  get subscription(): SubscriptionSummary {
    return this.subscriptionSubject.value;
  }

  isPremiumRequiredError(err: unknown): boolean {
    if (!err || typeof err !== 'object') return false;
    const status = (err as { status?: number }).status;
    return status === 403;
  }
}
