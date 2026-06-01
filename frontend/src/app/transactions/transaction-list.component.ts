import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TransactionService, PaymentTransaction } from '../core/services/transaction.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

@Component({
    selector: 'app-transaction-list',
    standalone: true,
    imports: [CommonModule, RouterLink, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
    templateUrl: './transaction-list.component.html',
    styleUrl: './transaction-list.component.css'
})
export class TransactionListComponent implements OnInit {
    transactions: PaymentTransaction[] = [];

    constructor(private transactionService: TransactionService) { }

    ngOnInit() {
        this.transactionService.getPaymentTransactions().subscribe(data => {
            this.transactions = data;
        });
    }

    isExpense(tx: PaymentTransaction): boolean {
        return (tx.transactionType || 'EXPENSE').toUpperCase() === 'EXPENSE';
    }
}
