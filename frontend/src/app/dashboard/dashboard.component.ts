import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GLASS_CARD_IMPORTS } from '../shared/shared-ui';
import { AccountService } from '../core/services/account.service';
import { ContractService } from '../core/services/contract.service';
import { InsightsService, CategorySpending, MonthlyTrend } from '../core/services/insights.service';
import { forkJoin } from 'rxjs';

const MONTH_NAMES = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, ...GLASS_CARD_IMPORTS],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  totalBalance = 0;
  fixedCosts = 0;
  disposableIncome = 0;
  accountCount = 0;
  contractCount = 0;
  spendingByCategory: CategorySpending[] = [];
  monthlyTrends: MonthlyTrend[] = [];
  maxCategoryAmount = 0;
  selectedYear = new Date().getFullYear();
  greeting = '';

  private static getGreeting(): string {
    const h = new Date().getHours();
    if (h < 12) return 'Guten Morgen';
    if (h < 18) return 'Guten Tag';
    return 'Guten Abend';
  }
  selectedMonth = new Date().getMonth() + 1;
  periodLabel = '';

  constructor(
    private accountService: AccountService,
    private contractService: ContractService,
    private insightsService: InsightsService
  ) {}

  ngOnInit() {
    this.greeting = DashboardComponent.getGreeting();
    forkJoin({
      accounts: this.accountService.getDepositAccounts(),
      contracts: this.contractService.getContracts(),
      insights: this.insightsService.getInsights(this.selectedYear, 0)
    }).subscribe(({ accounts, contracts, insights }) => {
      this.accountCount = accounts.length;
      this.totalBalance = accounts.reduce((sum, acc) => sum + acc.balance, 0);
      this.contractCount = contracts.length;
      this.fixedCosts = contracts.reduce((sum, contract) => sum + contract.amount, 0);
      this.disposableIncome = this.totalBalance - this.fixedCosts;

      this.spendingByCategory = insights.spendingByCategory;
      this.monthlyTrends = insights.monthlyTrends;
      this.maxCategoryAmount = Math.max(...this.spendingByCategory.map((c) => c.amount), 1);
      this.periodLabel = `${this.selectedYear}`;
    });
  }

  getBarWidth(amount: number): number {
    return this.maxCategoryAmount > 0 ? (amount / this.maxCategoryAmount) * 100 : 0;
  }

  getMonthLabel(year: number, month: number): string {
    return `${MONTH_NAMES[month - 1]} ${year}`;
  }

  getMonthExpenses(year: number, month: number): number {
    return this.monthlyTrends
      .filter((t) => t.year === year && t.month === month && t.transactionType === 'Expense')
      .reduce((s, t) => s + t.amount, 0);
  }

  getMonthIncome(year: number, month: number): number {
    return this.monthlyTrends
      .filter((t) => t.year === year && t.month === month && t.transactionType === 'Income')
      .reduce((s, t) => s + t.amount, 0);
  }

  get uniqueMonths(): { year: number; month: number }[] {
    const seen = new Set<string>();
    return this.monthlyTrends
      .map((t) => ({ year: t.year, month: t.month }))
      .filter((m) => {
        const k = `${m.year}-${m.month}`;
        if (seen.has(k)) return false;
        seen.add(k);
        return true;
      })
      .sort((a, b) => (a.year !== b.year ? a.year - b.year : a.month - b.month));
  }
}
