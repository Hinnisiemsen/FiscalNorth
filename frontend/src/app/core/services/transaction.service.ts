import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface TransferTransaction {
    id: number;
    amount: number;
    description: string;
    transactionDate: string;
    fromAccountId: number;
    toAccountId: number;
}

export interface PaymentTransaction {
    id: number;
    amount: number;
    description: string;
    transactionDate: string;
    transactionType: string;
    category?: { id: number; name: string };
    contract?: { id: number; name: string };
}

@Injectable({
    providedIn: 'root'
})
export class TransactionService {
    constructor(private apiService: ApiService) { }

    getPaymentTransactions(): Observable<PaymentTransaction[]> {
        return this.apiService.get<PaymentTransaction[]>('/transaction/payment');
    }

    getTransferTransactions(): Observable<TransferTransaction[]> {
        return this.apiService.get<TransferTransaction[]>('/transaction/transfer');
    }

    getPaymentTransactionsByCategory(categoryId: number): Observable<PaymentTransaction[]> {
        return this.apiService.get<PaymentTransaction[]>(`/transaction/payment/category/${categoryId}`);
    }

    createPaymentTransaction(transaction: any): Observable<PaymentTransaction> {
        return this.apiService.post<PaymentTransaction>('/transaction/payment', transaction);
    }

    importCsv(file: File, preset: string): Observable<CsvImportResult> {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('preset', preset);
        return this.apiService.postFormData<CsvImportResult>('/transaction/import/csv', formData);
    }
}

export interface CsvImportResult {
    imported: number;
    skippedDuplicates: number;
    parseErrors: number;
    errorMessages: string[];
}
