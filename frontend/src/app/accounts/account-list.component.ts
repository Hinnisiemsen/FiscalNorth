import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AccountService, UnifiedAccount } from '../core/services/account.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

@Component({
  selector: 'app-account-list',
  standalone: true,
  imports: [CommonModule, RouterLink, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
  templateUrl: './account-list.component.html',
  styleUrl: './account-list.component.css',
})
export class AccountListComponent implements OnInit {
  accounts: UnifiedAccount[] = [];

  constructor(private accountService: AccountService) {}

  ngOnInit() {
    this.loadAccounts();
  }

  loadAccounts() {
    this.accountService.getAllAccounts().subscribe((data) => {
      this.accounts = data;
    });
  }

  getCurrencyCode(currency: string): string {
    return currency === 'EURO' ? 'EUR' : currency;
  }

  accountIcon(account: UnifiedAccount): string {
    if (account.kind === 'DEPOSIT') {
      return 'savings';
    }
    if (account.kind === 'CRYPTO') {
      return 'currency_bitcoin';
    }
    switch (account.accountType) {
      case 'CRYPTO':
        return 'currency_bitcoin';
      case 'INVESTMENT':
        return 'trending_up';
      case 'CASH':
        return 'payments';
      case 'PAYPAL':
        return 'account_balance_wallet';
      case 'CREDIT_CARD':
        return 'credit_card';
      default:
        return 'account_balance';
    }
  }
}
