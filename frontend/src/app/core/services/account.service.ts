import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface DepositAccount {
    id: number;
    name: string;
    currency: string;
    balance: number;
    interestRate: number;
    term: string;
    renewable: boolean;
}

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    constructor(private apiService: ApiService) { }

    getDepositAccounts(): Observable<DepositAccount[]> {
        return this.apiService.get<DepositAccount[]>('/account/deposit');
    }

    createDepositAccount(account: any): Observable<DepositAccount> {
        return this.apiService.post<DepositAccount>('/account/deposit', account);
    }
}
