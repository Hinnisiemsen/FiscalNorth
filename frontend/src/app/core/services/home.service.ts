import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { PaymentTransaction } from './transaction.service';
import { BudgetWithUsage } from './budget.service';

export interface AccountSummary {
    id: number;
    name: string;
    currency: string;
    balance: number;
    interestRate?: number;
}

export interface HomeSummary {
    totalBalance: number;
    monthlyFixedCosts: number;
    disposableIncome: number;
    accounts: AccountSummary[];
    recentTransactions: PaymentTransaction[];
    budgets: BudgetWithUsage[];
    contractCount: number;
}

@Injectable({ providedIn: 'root' })
export class HomeService {
    constructor(private api: ApiService) {}

    getSummary(): Observable<HomeSummary> {
        return this.api.get<HomeSummary>('/home/summary');
    }
}
