import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { BankSyncService } from '../core/services/bank-sync.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';
import { LanguageService } from '../core/i18n/language.service';

@Component({
  selector: 'app-bank-sync',
  standalone: true,
  imports: [CommonModule, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
  templateUrl: './bank-sync.component.html',
  styleUrl: './bank-sync.component.css'
})
export class BankSyncComponent implements OnInit {
  private readonly lang = inject(LanguageService);

  status: { available: boolean; message: string } | null = null;
  consents: Array<{ id: number; consentId: string; status: string; validUntil: string }> = [];
  loading = false;
  error = '';
  redirectUrl = '';

  constructor(
    private bankSyncService: BankSyncService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

    ngOnInit() {
        this.loadStatus();
        this.loadConsents();

        this.route.queryParams.subscribe(params => {
            const err = params['error'];
            if (err === 'callback_failed') {
                this.error = this.lang.instant('bankSync.callbackFailed');
            } else if (err) {
                this.error = err;
            }
        });
    }

  loadStatus() {
    this.bankSyncService.getStatus().subscribe(s => {
      this.status = s;
    });
  }

  loadConsents() {
    this.bankSyncService.getConsents().subscribe(c => {
      this.consents = c.map(x => ({
        id: x.id,
        consentId: x.consentId,
        status: x.status,
        validUntil: x.validUntil || ''
      }));
    });
  }

  connectBank() {
    this.loading = true;
    this.error = '';
    this.bankSyncService.createConsent().subscribe({
      next: res => {
        this.loading = false;
        if (res.redirectUrl) {
          window.location.href = res.redirectUrl;
        } else {
          this.error = res.message || this.lang.instant('bankSync.noRedirect');
        }
      },
      error: err => {
        this.loading = false;
        this.error = err.error?.message || err.message || this.lang.instant('bankSync.consentFailed');
      }
    });
  }

  handleCallback(consentId: string) {
    this.bankSyncService.handleCallback(consentId).subscribe({
      next: res => {
        this.router.navigateByUrl(res.redirectTo || '/accounts');
      },
      error: () => {
        this.router.navigate(['/bank-sync'], { queryParams: { error: 'callback_failed' } });
      }
    });
  }

  sync(consentId: string) {
    this.loading = true;
    this.error = '';
    this.bankSyncService.sync(consentId).subscribe({
      next: res => {
        this.loading = false;
        if (res.success) {
          this.loadConsents();
          this.router.navigate(['/accounts']);
        } else {
          this.error = res.message;
        }
      },
      error: err => {
        this.loading = false;
        this.error = err.error?.message || err.message || this.lang.instant('bankSync.syncFailed');
      }
    });
  }
}
