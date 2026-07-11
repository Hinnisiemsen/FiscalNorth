import { Component, Input, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';
import { PremiumFeature } from '../core/models/billing.model';
import { EntitlementService } from '../core/services/entitlement.service';

@Component({
  selector: 'app-paywall-banner',
  standalone: true,
  imports: [RouterLink, ...TRANSLATE_IMPORTS],
  templateUrl: './paywall-banner.component.html',
  styleUrl: './paywall-banner.component.css',
})
export class PaywallBannerComponent {
  @Input({ required: true }) feature!: PremiumFeature;
  @Input() messageKey = 'billing.paywall.default';

  private readonly entitlementService = inject(EntitlementService);

  get showTryDemo(): boolean {
    return this.entitlementService.subscription.premiumPreviewEnabled === true;
  }

  tryDemo(): void {
    this.entitlementService.dismissPremiumBanner();
  }
}
