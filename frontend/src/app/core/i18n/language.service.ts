import { Injectable, LOCALE_ID, inject, signal } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    AppLocale,
    DEFAULT_LOCALE,
    LOCALE_STORAGE_KEY,
    intlLocaleTag,
    isAppLocale,
    resolveInitialLocale,
} from './supported-locales';

@Injectable({ providedIn: 'root' })
export class LanguageService {
    private readonly translate = inject(TranslateService);
    private readonly localeId = inject(LOCALE_ID, { optional: true });

    readonly current = signal<AppLocale>(resolveInitialLocale());

    constructor() {
        const initial = this.current();
        this.translate.setDefaultLang(DEFAULT_LOCALE);
        this.translate.use(initial);
        this.syncDocument(initial);
    }

    setLanguage(code: AppLocale): void {
        if (!isAppLocale(code)) {
            return;
        }
        this.current.set(code);
        localStorage.setItem(LOCALE_STORAGE_KEY, code);
        this.translate.use(code);
        this.syncDocument(code);
        if (this.localeId && typeof this.localeId === 'string') {
            // LOCALE_ID is provided via factory in app.config
        }
    }

    intlLocale(): string {
        return intlLocaleTag(this.current());
    }

    instant(key: string, params?: Record<string, unknown>): string {
        return this.translate.instant(key, params);
    }

    private syncDocument(code: AppLocale): void {
        if (typeof document !== 'undefined') {
            document.documentElement.lang = code;
        }
    }
}
