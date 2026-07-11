import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AccountService, UnifiedAccount } from '../core/services/account.service';
import { HomeService } from '../core/services/home.service';
import { PaymentTransaction } from '../core/services/transaction.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { SkeletonComponent } from '../shared/skeleton.component';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

@Component({
  selector: 'app-account-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    SkeletonComponent,
    ...PAGE_HEADER_IMPORTS,
    ...TRANSLATE_IMPORTS,
  ],
  templateUrl: './account-detail.component.html',
  styleUrl: './account-detail.component.css',
})
export class AccountDetailComponent implements OnInit {
  account: UnifiedAccount | null = null;
  recentTransactions: PaymentTransaction[] = [];
  loading = true;

  constructor(
    private route: ActivatedRoute,
    private accountService: AccountService,
    private homeService: HomeService,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.accountService.getAccountById(id).subscribe({
      next: (account) => {
        this.account = account ?? null;
        this.loading = false;
      },
      error: () => (this.loading = false),
    });
    this.homeService.getSummary().subscribe({
      next: (s) => (this.recentTransactions = s.recentTransactions.slice(0, 8)),
    });
  }

  getCurrencyCode(currency: string): string {
    return currency === 'EURO' ? 'EUR' : currency;
  }
}
