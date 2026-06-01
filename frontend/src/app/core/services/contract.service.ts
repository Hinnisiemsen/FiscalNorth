import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Contract {
    id: number;
    name: string;
    startDate: string;
    endDate: string;
    amount: number;
    contractInterval: string;
    autoDetected: boolean;
}

@Injectable({
    providedIn: 'root'
})
export class ContractService {
    constructor(private apiService: ApiService) { }

    getContracts(): Observable<Contract[]> {
        return this.apiService.get<Contract[]>('/contract');
    }

    createContract(contract: any): Observable<Contract> {
        return this.apiService.post<Contract>('/contract', contract);
    }

    analyzeContracts(): Observable<string> {
        return this.apiService.post<string>('/contract/analyze', {});
    }
}
