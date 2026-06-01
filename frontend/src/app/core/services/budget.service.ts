import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Budget {
    id: number;
    name: string;
    limit: number;
    startDate: string;
    endDate: string;
}

export interface BudgetWithUsage extends Budget {
    spent: number;
    categoryId?: number;
    categoryName?: string;
}

@Injectable({
    providedIn: 'root'
})
export class BudgetService {
    constructor(private apiService: ApiService) { }

    getBudgets(): Observable<Budget[]> {
        return this.apiService.get<Budget[]>('/budget');
    }

    getBudgetsWithUsage(): Observable<BudgetWithUsage[]> {
        return this.apiService.get<BudgetWithUsage[]>('/budget/with-usage');
    }

    createBudget(budget: any): Observable<Budget> {
        return this.apiService.post<Budget>('/budget', budget);
    }
}
