import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { BillingService } from './billing.service';
import { PremiumFeature, SubscriptionSummary } from '../models/billing.model';
import { UserProfile } from './user.service';

const DEMO_PAYWALL_DISMISSED_KEY = 'fiscalnorth.demoPaywallDismissed';

const EMPTY_SUBSCRIPTION: SubscriptionSummary = {
  plan: 'FREE',
  status: 'NONE',
  entitlements: [],
  currentPeriodEnd: null,
  trialEnd: null,
  cancelAtPeriodEnd: false,
  premiumActive: false,
  billingEnabled: false,
  premiumPreviewEnabled: false,
  paidSubscriptionActive: false,
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
      this.subscriptionSubject.next({ ...this.subscriptionSubject.value, ...profile.subscription });
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
          billingEnabled: response.billingEnabled,
          premiumPreviewEnabled: response.premiumPreviewEnabled,
          paidSubscriptionActive: response.paidSubscriptionActive,
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

  get showPremiumBanner(): boolean {
    const sub = this.subscriptionSubject.value;
    if (sub.paidSubscriptionActive) {
      return false;
    }
    if (sessionStorage.getItem(DEMO_PAYWALL_DISMISSED_KEY) === 'true') {
      return false;
    }
    return !!(sub.premiumPreviewEnabled || sub.billingEnabled);
  }

  dismissPremiumBanner(): void {
    sessionStorage.setItem(DEMO_PAYWALL_DISMISSED_KEY, 'true');
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
