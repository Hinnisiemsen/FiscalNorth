import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, of, switchMap } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.currentUser) {
    return true;
  }

  return auth.refreshSession().pipe(
    switchMap((user) => {
      if (user) {
        return of(true);
      }
      return router.createUrlTree(['/login']);
    }),
    map((result) => result)
  );
};
