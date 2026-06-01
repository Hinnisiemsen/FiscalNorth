import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { BudgetService } from '../core/services/budget.service';
import { CategoryService, Category } from '../core/services/category.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

@Component({
  selector: 'app-budget-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
  templateUrl: './budget-create.component.html',
  styleUrl: './budget-create.component.css',
})
export class BudgetCreateComponent implements OnInit {
  budgetForm: FormGroup;
  expenseCategories: Category[] = [];

  constructor(
    private fb: FormBuilder,
    private budgetService: BudgetService,
    private categoryService: CategoryService,
    private router: Router,
  ) {
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1).toISOString().split('T')[0];
    const lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0)
      .toISOString()
      .split('T')[0];

    this.budgetForm = this.fb.group({
      name: ['', Validators.required],
      limit: ['', [Validators.required, Validators.min(0.01)]],
      startDate: [firstDay, Validators.required],
      endDate: [lastDay, Validators.required],
      categoryId: [null],
    });
  }

  ngOnInit() {
    this.categoryService.getCategories().subscribe((cats) => {
      this.expenseCategories = cats.filter((c) => c.transactionType === 'Expense');
    });
  }

  onSubmit() {
    if (this.budgetForm.valid) {
      const value = { ...this.budgetForm.value };
      if (!value.categoryId) delete value.categoryId;
      this.budgetService.createBudget(value).subscribe({
        next: () => this.router.navigate(['/budgets']),
        error: (err) => console.error('Failed to create budget', err),
      });
    }
  }

  cancel() {
    this.router.navigate(['/budgets']);
  }
}
