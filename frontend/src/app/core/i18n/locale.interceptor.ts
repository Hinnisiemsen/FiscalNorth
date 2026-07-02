import { HttpInterceptorFn } from '@angular/common/http';
import { resolveInitialLocale } from './supported-locales';

export const localeInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.includes('/assets/')) {
    return next(req);
  }

  return next(
    req.clone({
      setHeaders: {
        'Accept-Language': resolveInitialLocale(),
      },
    }),
  );
};
