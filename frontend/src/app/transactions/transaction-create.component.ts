import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  FormArray,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { TransactionService } from '../core/services/transaction.service';
import { CategoryService, Category } from '../core/services/category.service';
import { ContractService, Contract } from '../core/services/contract.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

@Component({
  selector: 'app-transaction-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
  templateUrl: './transaction-create.component.html',
  styleUrl: './transaction-create.component.css',
})
export class TransactionCreateComponent implements OnInit {
  transactionForm: FormGroup;
  categories: Category[] = [];
  contracts: Contract[] = [];
  splitEnabled = false;

  constructor(
    private fb: FormBuilder,
    private transactionService: TransactionService,
    private categoryService: CategoryService,
    private contractService: ContractService,
    private router: Router,
  ) {
    this.transactionForm = this.fb.group({
      description: ['', Validators.required],
      amount: ['', [Validators.required, Validators.min(0.01)]],
      transactionDate: [new Date().toISOString().split('T')[0], Validators.required],
      transactionType: ['Expense', Validators.required],
      categoryId: [null],
      contractId: [null],
      splits: this.fb.array([]),
    });
  }

  get splits(): FormArray {
    return this.transactionForm.get('splits') as FormArray;
  }

  ngOnInit() {
    this.categoryService.getCategories().subscribe((data) => (this.categories = data));
    this.contractService.getContracts().subscribe((data) => (this.contracts = data));
  }

  toggleSplit(enabled: boolean): void {
    this.splitEnabled = enabled;
    if (enabled && this.splits.length === 0) {
      this.addSplitLine();
      this.addSplitLine();
    }
    if (!enabled) {
      this.splits.clear();
    }
  }

  addSplitLine(): void {
    this.splits.push(
      this.fb.group({
        amount: ['', [Validators.required, Validators.min(0.01)]],
        categoryId: [null, Validators.required],
        note: [''],
      }),
    );
  }

  removeSplitLine(index: number): void {
    this.splits.removeAt(index);
  }

  splitTotal(): number {
    return this.splits.controls.reduce((sum, control) => {
      const value = Number(control.get('amount')?.value || 0);
      return sum + value;
    }, 0);
  }

  splitTotalMismatch(): boolean {
    const amount = Number(this.transactionForm.get('amount')?.value || 0);
    return this.splitEnabled && Math.abs(this.splitTotal() - amount) > 0.009;
  }

  onSubmit() {
    if (this.transactionForm.invalid) {
      return;
    }

    const val = this.transactionForm.value;
    const amount = Number(val.amount);

    if (this.splitEnabled) {
      const splitTotal = this.splitTotal();
      if (Math.abs(splitTotal - amount) > 0.009) {
        return;
      }
    }

    const payload: Record<string, unknown> = {
      description: val.description,
      amount: val.amount,
      transactionDate: val.transactionDate,
      transactionType: val.transactionType,
      category: !this.splitEnabled && val.categoryId ? { id: val.categoryId } : null,
      contract: val.contractId ? { id: val.contractId } : null,
    };

    if (this.splitEnabled && this.splits.length > 0) {
      payload['splits'] = this.splits.controls.map((control) => ({
        amount: Number(control.get('amount')?.value),
        categoryId: control.get('categoryId')?.value,
        note: control.get('note')?.value || null,
      }));
    }

    this.transactionService.createPaymentTransaction(payload).subscribe({
      next: () => this.router.navigate(['/transactions']),
      error: (err) => console.error('Failed to create transaction', err),
    });
  }

  cancel() {
    this.router.navigate(['/transactions']);
  }
}
