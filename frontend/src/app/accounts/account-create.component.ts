import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AccountService, BankAccountType } from '../core/services/account.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

@Component({
  selector: 'app-account-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
  templateUrl: './account-create.component.html',
  styleUrl: './account-create.component.css',
})
export class AccountCreateComponent {
  accountKind: 'DEPOSIT' | 'BANK' = 'DEPOSIT';
  depositForm: FormGroup;
  bankForm: FormGroup;

  readonly bankAccountTypes: BankAccountType[] = [
    'CHECKING',
    'SAVINGS',
    'CREDIT_CARD',
    'CASH',
    'INVESTMENT',
    'LOAN',
    'PAYPAL',
    'CRYPTO',
    'PENSION',
    'INSURANCE',
    'BUSINESS',
    'PREPAID',
    'OTHER',
  ];

  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    private router: Router,
  ) {
    this.depositForm = this.fb.group({
      name: ['', Validators.required],
      currency: ['EURO', Validators.required],
      balance: [0, [Validators.required, Validators.min(0)]],
      interestRate: [0, [Validators.required, Validators.min(0)]],
      term: ['Flexible', Validators.required],
      renewable: [false],
    });

    this.bankForm = this.fb.group({
      name: ['', Validators.required],
      bankName: ['', Validators.required],
      iban: ['', [Validators.required, Validators.minLength(15), Validators.maxLength(34)]],
      bic: ['', [Validators.required, Validators.pattern(/^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$/)]],
      accountType: ['CHECKING', Validators.required],
      currency: ['EURO', Validators.required],
      balance: [0, [Validators.required, Validators.min(0)]],
    });
  }

  setAccountKind(kind: 'DEPOSIT' | 'BANK'): void {
    this.accountKind = kind;
  }

  onSubmit() {
    if (this.accountKind === 'DEPOSIT' && this.depositForm.valid) {
      this.accountService.createDepositAccount(this.depositForm.value).subscribe({
        next: () => this.router.navigate(['/accounts']),
        error: (err) => console.error('Failed to create deposit account', err),
      });
      return;
    }

    if (this.accountKind === 'BANK' && this.bankForm.valid) {
      const value = this.bankForm.value;
      this.accountService
        .createBankAccount({
          bankName: value.bankName,
          iban: value.iban.toUpperCase(),
          bic: value.bic.toUpperCase(),
          accountType: value.accountType,
          name: value.name,
          currency: value.currency,
          balance: value.balance,
        })
        .subscribe({
          next: () => this.router.navigate(['/accounts']),
          error: (err) => console.error('Failed to create bank account', err),
        });
    }
  }

  cancel() {
    this.router.navigate(['/accounts']);
  }
}
