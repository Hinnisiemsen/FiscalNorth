import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

function readCookie(name: string): string | null {
  const match = document.cookie.match(
    new RegExp('(?:^|; )' + name.replace(/[.$?*|{}()[\]\\/+^]/g, '\\$&') + '=([^;]*)'),
  );
  return match ? decodeURIComponent(match[1]) : null;
}

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  let headers = req.headers;
  const csrf = readCookie('XSRF-TOKEN');
  if (csrf && req.method !== 'GET' && req.method !== 'HEAD') {
    headers = headers.set('X-XSRF-TOKEN', csrf);
  }

  const locale = authService.currentUser?.locale;
  if (locale) {
    headers = headers.set('Accept-Language', locale);
  }

  const cloned = req.clone({
    withCredentials: true,
    headers,
  });

  return next(cloned).pipe(
    catchError((err) => {
      if (err.status === 401 && !req.url.includes('/api/auth/')) {
        router.navigate(['/login']);
      }
      return throwError(() => err);
    }),
  );
};
