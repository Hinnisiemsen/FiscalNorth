import { Injectable } from '@angular/core';
import { forkJoin, map, Observable } from 'rxjs';
import { ApiService } from './api.service';

export type AccountKind = 'DEPOSIT' | 'BANK' | 'CRYPTO';

export type BankAccountType =
  | 'CHECKING'
  | 'SAVINGS'
  | 'CREDIT_CARD'
  | 'CASH'
  | 'INVESTMENT'
  | 'LOAN'
  | 'PAYPAL'
  | 'CRYPTO'
  | 'PENSION'
  | 'INSURANCE'
  | 'BUSINESS'
  | 'PREPAID'
  | 'OTHER';

export interface DepositAccount {
  id: number;
  name: string;
  currency: string;
  balance: number;
  interestRate: number;
  term: string;
  renewable: boolean;
}

export interface BankAccount {
  id: number;
  name: string;
  currency: string;
  balance: number;
  bankName: string;
  iban: string;
  bic: string;
  accountType: BankAccountType;
}

export interface CryptoAccount {
  id: number;
  name: string;
  currency: string;
  balance: number;
  walletAddress: string;
  provider?: string;
}

export interface UnifiedAccount {
  id: number;
  kind: AccountKind;
  name: string;
  currency: string;
  balance: number;
  interestRate?: number;
  term?: string;
  renewable?: boolean;
  bankName?: string;
  iban?: string;
  bic?: string;
  accountType?: BankAccountType;
  walletAddress?: string;
  provider?: string;
}

export interface CreateDepositAccountPayload {
  name: string;
  currency: string;
  balance: number;
  interestRate: number;
  term: string;
  renewable: boolean;
}

export interface CreateBankAccountPayload {
  bankName: string;
  iban: string;
  bic: string;
  accountType: BankAccountType;
  name?: string;
  currency?: string;
  balance?: number;
}

export interface CreateCryptoAccountPayload {
  name: string;
  walletAddress: string;
  provider?: string;
  currency: string;
  balance: number;
}

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  constructor(private apiService: ApiService) {}

  getDepositAccounts(): Observable<DepositAccount[]> {
    return this.apiService.get<DepositAccount[]>('/account/deposit');
  }

  getBankAccounts(): Observable<BankAccount[]> {
    return this.apiService.get<BankAccount[]>('/account/bank');
  }

  getCryptoAccounts(): Observable<CryptoAccount[]> {
    return this.apiService.get<CryptoAccount[]>('/account/crypto');
  }

  createCryptoAccount(account: CreateCryptoAccountPayload): Observable<CryptoAccount> {
    return this.apiService.post<CryptoAccount>('/account/crypto', account);
  }

  getAllAccounts(): Observable<UnifiedAccount[]> {
    return forkJoin({
      deposit: this.getDepositAccounts(),
      bank: this.getBankAccounts(),
      crypto: this.getCryptoAccounts(),
    }).pipe(
      map(({ deposit, bank, crypto }) => [
        ...deposit.map((a) => this.toUnifiedDeposit(a)),
        ...bank.map((a) => this.toUnifiedBank(a)),
        ...crypto.map((a) => this.toUnifiedCrypto(a)),
      ]),
    );
  }

  getAccountById(id: number): Observable<UnifiedAccount | undefined> {
    return this.getAllAccounts().pipe(map((accounts) => accounts.find((a) => a.id === id)));
  }

  createDepositAccount(account: CreateDepositAccountPayload): Observable<DepositAccount> {
    return this.apiService.post<DepositAccount>('/account/deposit', account);
  }

  createBankAccount(account: CreateBankAccountPayload): Observable<BankAccount> {
    return this.apiService.post<BankAccount>('/account/bank', account);
  }

  deleteDepositAccount(id: number): Observable<void> {
    return this.apiService.delete<void>(`/account/deposit/${id}`);
  }

  private toUnifiedDeposit(account: DepositAccount): UnifiedAccount {
    return {
      id: account.id,
      kind: 'DEPOSIT',
      name: account.name,
      currency: account.currency,
      balance: account.balance,
      interestRate: account.interestRate,
      term: account.term,
      renewable: account.renewable,
    };
  }

  private toUnifiedBank(account: BankAccount): UnifiedAccount {
    return {
      id: account.id,
      kind: 'BANK',
      name: account.name,
      currency: account.currency,
      balance: account.balance,
      bankName: account.bankName,
      iban: account.iban,
      bic: account.bic,
      accountType: account.accountType,
    };
  }

  private toUnifiedCrypto(account: CryptoAccount): UnifiedAccount {
    return {
      id: account.id,
      kind: 'CRYPTO',
      name: account.name,
      currency: account.currency,
      balance: account.balance,
      walletAddress: account.walletAddress,
      provider: account.provider,
    };
  }
}
