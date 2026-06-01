import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export type AuthProvider = 'LOCAL' | 'GOOGLE' | 'BOTH';

export interface UserProfile {
    id: number | null;
    userName: string;
    email: string | null;
    avatarUrl: string | null;
    authProvider: AuthProvider;
    locale: string;
}

export interface UpdateUserProfileRequest {
    userName: string;
    locale: string;
}

export interface ChangePasswordRequest {
    currentPassword: string;
    newPassword: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {
    constructor(private api: ApiService) {}

    getCurrentUser(): Observable<UserProfile> {
        return this.api.get<UserProfile>('/user/me');
    }

    updateProfile(body: UpdateUserProfileRequest): Observable<UserProfile> {
        return this.api.put<UserProfile>('/user/me', body);
    }

    changePassword(body: ChangePasswordRequest): Observable<void> {
        return this.api.put<void>('/user/me/password', body);
    }
}
