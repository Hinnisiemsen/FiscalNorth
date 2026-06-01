import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { UserProfile, UserService } from './user.service';

export interface AuthStatus {
  authenticated: boolean;
  provider: 'LOCAL' | 'GOOGLE' | 'BOTH' | null;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly userService = inject(UserService);

  private readonly currentUserSubject = new BehaviorSubject<UserProfile | null>(null);
  readonly currentUser$ = this.currentUserSubject.asObservable();

  login(email: string, password: string): Observable<UserProfile> {
    return this.http
      .post<UserProfile>('/api/auth/login', { email, password }, { withCredentials: true })
      .pipe(tap((profile) => this.currentUserSubject.next(profile)));
  }

  register(userName: string, email: string, password: string): Observable<UserProfile> {
    return this.http
      .post<UserProfile>('/api/auth/register', { userName, email, password }, { withCredentials: true })
      .pipe(tap((profile) => this.currentUserSubject.next(profile)));
  }

  logout(): Observable<void> {
    return this.http.post<void>('/api/auth/logout', {}, { withCredentials: true }).pipe(
      tap(() => {
        this.currentUserSubject.next(null);
        this.router.navigate(['/login']);
      })
    );
  }

  status(): Observable<AuthStatus> {
    return this.http.get<AuthStatus>('/api/auth/status', { withCredentials: true });
  }

  loadCurrentUser(): Observable<UserProfile> {
    return this.userService.getCurrentUser().pipe(
      tap((profile) => this.currentUserSubject.next(profile))
    );
  }

  refreshSession(): Observable<UserProfile | null> {
    return new Observable((subscriber) => {
      this.status().subscribe({
        next: (status) => {
          if (!status.authenticated) {
            this.currentUserSubject.next(null);
            subscriber.next(null);
            subscriber.complete();
            return;
          }
          this.loadCurrentUser().subscribe({
            next: (profile) => {
              subscriber.next(profile);
              subscriber.complete();
            },
            error: () => {
              this.currentUserSubject.next(null);
              subscriber.next(null);
              subscriber.complete();
            },
          });
        },
        error: () => {
          this.currentUserSubject.next(null);
          subscriber.next(null);
          subscriber.complete();
        },
      });
    });
  }

  get currentUser(): UserProfile | null {
    return this.currentUserSubject.value;
  }

  loginWithGoogle(): void {
    window.location.href = '/oauth2/authorization/google';
  }
}
