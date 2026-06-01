import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
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
    });
  }

  ngOnInit() {
    this.categoryService.getCategories().subscribe((data) => (this.categories = data));
    this.contractService.getContracts().subscribe((data) => (this.contracts = data));
  }

  onSubmit() {
    if (this.transactionForm.valid) {
      const val = this.transactionForm.value;
      const payload = {
        description: val.description,
        amount: val.amount,
        transactionDate: val.transactionDate,
        transactionType: val.transactionType,
        category: val.categoryId ? { id: val.categoryId } : null,
        contract: val.contractId ? { id: val.contractId } : null,
      };
      this.transactionService.createPaymentTransaction(payload).subscribe({
        next: () => this.router.navigate(['/transactions']),
        error: (err) => console.error('Failed to create transaction', err),
      });
    }
  }

  cancel() {
    this.router.navigate(['/transactions']);
  }
}
