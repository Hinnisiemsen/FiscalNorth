import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { LanguageService } from './language.service';

export const localeInterceptor: HttpInterceptorFn = (req, next) => {
    const language = inject(LanguageService);
    return next(
        req.clone({
            setHeaders: {
                'Accept-Language': language.current(),
            },
        })
    );
};
