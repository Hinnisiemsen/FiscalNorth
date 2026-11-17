import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface HouseholdMember {
  userId: number;
  userName: string;
  email: string;
  role: string;
  joinedAt: string;
}

export interface HouseholdInvite {
  id: number;
  email: string;
  token: string;
  expiresAt: string;
  status: string;
}

export interface Household {
  id: number;
  name: string;
  members: HouseholdMember[];
  pendingInvite: HouseholdInvite | null;
}

@Injectable({ providedIn: 'root' })
export class HouseholdService {
  constructor(private api: ApiService) {}

  getMyHousehold(): Observable<Household> {
    return this.api.get<Household>('/household/me');
  }

  invitePartner(email: string): Observable<HouseholdInvite> {
    return this.api.post<HouseholdInvite>('/household/invite', { email });
  }

  acceptInvite(token: string): Observable<Household> {
    return this.api.post<Household>(
      `/household/invites/accept?token=${encodeURIComponent(token)}`,
      {},
    );
  }
}
