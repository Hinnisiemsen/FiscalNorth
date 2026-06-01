import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AccountService } from '../core/services/account.service';
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
  accountForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    private router: Router,
  ) {
    this.accountForm = this.fb.group({
      name: ['', Validators.required],
      currency: ['EURO', Validators.required],
      balance: [0, [Validators.required, Validators.min(0)]],
      interestRate: [0, [Validators.required, Validators.min(0)]],
      term: ['Flexible', Validators.required],
      renewable: [false],
    });
  }

  onSubmit() {
    if (this.accountForm.valid) {
      this.accountService.createDepositAccount(this.accountForm.value).subscribe({
        next: () => this.router.navigate(['/accounts']),
        error: (err) => console.error('Failed to create account', err),
      });
    }
  }

  cancel() {
    this.router.navigate(['/accounts']);
  }
}
