import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface CategorySpending {
    categoryName: string;
    amount: number;
}

export interface MonthlyTrend {
    year: number;
    month: number;
    transactionType: string;
    amount: number;
}

export interface InsightsResponse {
    spendingByCategory: CategorySpending[];
    monthlyTrends: MonthlyTrend[];
    periodStart: string;
    periodEnd: string;
}

@Injectable({ providedIn: 'root' })
export class InsightsService {
    constructor(private apiService: ApiService) {}

    getInsights(year?: number, month?: number): Observable<InsightsResponse> {
        const params: string[] = [];
        if (year) params.push(`year=${year}`);
        if (month) params.push(`month=${month}`);
        const qs = params.length ? '?' + params.join('&') : '';
        return this.apiService.get<InsightsResponse>(`/insights${qs}`);
    }
}
