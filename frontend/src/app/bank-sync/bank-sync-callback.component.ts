import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { BankSyncService } from '../core/services/bank-sync.service';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';
import { LanguageService } from '../core/i18n/language.service';

@Component({
    selector: 'app-bank-sync-callback',
    standalone: true,
    imports: [CommonModule, ...TRANSLATE_IMPORTS],
    template: `
        <div class="page-content-inner centered-message">
            <span class="spark spark-lg" aria-hidden="true"></span>
            <p *ngIf="!error">{{ 'bankSync.callbackProcessing' | translate }}</p>
            <p class="error-message" *ngIf="error">{{ error }}</p>
        </div>
    `,
})
export class BankSyncCallbackComponent implements OnInit {
    private readonly lang = inject(LanguageService);

    error = '';

    constructor(
        private bankSyncService: BankSyncService,
        private route: ActivatedRoute,
        private router: Router
    ) {}

    ngOnInit() {
        this.route.queryParams.subscribe(params => {
            const consentId = params['consentId'] ?? params['consent_id'] ?? '';
            if (consentId) {
                this.bankSyncService.handleCallback(consentId).subscribe({
                    next: res => this.router.navigateByUrl(res.redirectTo || '/accounts'),
                    error: () => this.router.navigate(['/bank-sync'], { queryParams: { error: 'callback_failed' } })
                });
            } else {
                this.error = this.lang.instant('bankSync.callbackNoConsent');
                setTimeout(() => this.router.navigate(['/bank-sync']), 3000);
            }
        });
    }
}
