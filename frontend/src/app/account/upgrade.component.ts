import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { BillingService } from '../core/services/billing.service';
import { EntitlementService } from '../core/services/entitlement.service';
import { BillingPlan } from '../core/models/billing.model';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

@Component({
  selector: 'app-upgrade',
  standalone: true,
  imports: [CommonModule, RouterLink, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
  templateUrl: './upgrade.component.html',
  styleUrl: './upgrade.component.css',
})
export class UpgradeComponent implements OnInit {
  private readonly billingService = inject(BillingService);
  private readonly entitlementService = inject(EntitlementService);
  private readonly route = inject(ActivatedRoute);

  plans: BillingPlan[] = [];
  billingEnabled = false;
  selectedInterval: 'month' | 'year' = 'month';
  loading = false;
  error = '';
  checkoutCanceled = false;

  readonly premiumFeatures = [
    'billing.features.aiAssistant',
    'billing.features.aiGoalPlanner',
    'billing.features.bankSync',
    'billing.features.aiNotifications',
  ] as const;

  ngOnInit(): void {
    this.checkoutCanceled = this.route.snapshot.queryParamMap.get('checkout') === 'canceled';
    this.billingService.getPlans().subscribe({
      next: (response) => {
        this.plans = response.plans;
        this.billingEnabled = response.billingEnabled;
        if (response.plans.some((p) => p.interval === 'year')) {
          this.selectedInterval = 'year';
        }
      },
    });
  }

  get selectedPlan(): BillingPlan | undefined {
    return this.plans.find((p) => p.interval === this.selectedInterval);
  }

  get isPremium(): boolean {
    return this.entitlementService.isPremium;
  }

  startCheckout(): void {
    const plan = this.selectedPlan;
    if (!plan) {
      this.error = 'billing.checkoutUnavailable';
      return;
    }
    this.loading = true;
    this.error = '';
    this.billingService.createCheckoutSession(plan.priceId).subscribe({
      next: (session) => {
        window.location.href = session.url;
      },
      error: () => {
        this.loading = false;
        this.error = 'billing.checkoutFailed';
      },
    });
  }
}
