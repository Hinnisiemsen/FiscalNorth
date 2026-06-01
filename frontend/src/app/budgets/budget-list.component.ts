import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BudgetService, BudgetWithUsage } from '../core/services/budget.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';

@Component({
  selector: 'app-budget-list',
  standalone: true,
  imports: [CommonModule, RouterLink, ...PAGE_HEADER_IMPORTS],
  templateUrl: './budget-list.component.html',
  styleUrl: './budget-list.component.css'
})
export class BudgetListComponent implements OnInit {
  budgets: BudgetWithUsage[] = [];

  constructor(private budgetService: BudgetService) { }

  ngOnInit() {
    this.budgetService.getBudgetsWithUsage().subscribe(data => {
      this.budgets = data;
    });
  }

  getProgressPercent(budget: BudgetWithUsage): number {
    if (!budget.limit || budget.limit <= 0) return 0;
    return Math.min(100, (budget.spent / budget.limit) * 100);
  }

  getProgressBarClass(budget: BudgetWithUsage): string {
    const pct = this.getProgressPercent(budget);
    if (pct >= 100) return 'over';
    if (pct >= 80) return 'warning';
    return 'ok';
  }
}
