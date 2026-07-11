import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TransactionService, PaymentTransaction } from '../core/services/transaction.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [CommonModule, RouterLink, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
  templateUrl: './transaction-list.component.html',
  styleUrl: './transaction-list.component.css',
})
export class TransactionListComponent implements OnInit {
  transactions: PaymentTransaction[] = [];
  filteredTransactions: PaymentTransaction[] = [];
  categoryFilter = '';
  monthFilter = '';

  constructor(
    private transactionService: TransactionService,
    private route: ActivatedRoute,
  ) {}

  ngOnInit() {
    this.route.queryParamMap.subscribe((params) => {
      this.categoryFilter = params.get('category') ?? '';
      const year = params.get('year');
      const month = params.get('month');
      this.monthFilter = year && month ? `${year}-${month.padStart(2, '0')}` : '';
      this.applyFilter();
    });

    this.transactionService.getPaymentTransactions().subscribe((data) => {
      this.transactions = data;
      this.applyFilter();
    });
  }

  private applyFilter(): void {
    this.filteredTransactions = this.transactions.filter((tx) => {
      const matchesCategory =
        !this.categoryFilter ||
        tx.category?.name === this.categoryFilter ||
        tx.splits?.some((s) => s.category?.name === this.categoryFilter);
      const matchesMonth =
        !this.monthFilter || tx.transactionDate?.startsWith(this.monthFilter);
      return matchesCategory && matchesMonth;
    });
  }

  isExpense(tx: PaymentTransaction): boolean {
    return (tx.transactionType || 'EXPENSE').toUpperCase() === 'EXPENSE';
  }
}
