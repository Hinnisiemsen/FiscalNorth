import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';

export interface BankSyncStatus {
  available: boolean;
  message: string;
}

export interface CreateConsentResponse {
  consentId: string | null;
  redirectUrl: string | null;
  message: string | null;
}

export interface BankConsent {
  id: number;
  consentId: string;
  psuId: string;
  status: string;
  validUntil: string | null;
  createdAt: string;
}

export interface HandleCallbackResponse {
  redirectTo: string;
}

export interface SyncResult {
  success: boolean;
  accountsCreated: number;
  transactionsCreated: number;
  message: string;
}

@Injectable({ providedIn: 'root' })
export class BankSyncService {
  constructor(private api: ApiService) {}

  getStatus(): Observable<BankSyncStatus> {
    return this.api.get<BankSyncStatus>('/bank-sync/status');
  }

  createConsent(): Observable<CreateConsentResponse> {
    return this.api.post<CreateConsentResponse>('/bank-sync/consent');
  }

  handleCallback(consentId: string): Observable<HandleCallbackResponse> {
    return this.api.get<HandleCallbackResponse>(
      '/bank-sync/callback',
      new HttpParams({ fromObject: { consentId } }),
    );
  }

  sync(consentId: string): Observable<SyncResult> {
    return this.api.post<SyncResult>('/bank-sync/sync', { consentId });
  }

  getConsents(): Observable<BankConsent[]> {
    return this.api.get<BankConsent[]>('/bank-sync/consents');
  }
}
