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

@Injectable({
    providedIn: 'root'
})
export class BudgetService {
    constructor(private apiService: ApiService) { }

    getBudgets(): Observable<Budget[]> {
        return this.apiService.get<Budget[]>('/budget');
    }

    createBudget(budget: any): Observable<Budget> {
        return this.apiService.post<Budget>('/budget', budget);
    }
}
