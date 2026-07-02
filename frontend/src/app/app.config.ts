import {
  ApplicationConfig,
  importProvidersFrom,
  LOCALE_ID,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { TranslateModule } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';
import { routes } from './app.routes';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { localeInterceptor } from './core/i18n/locale.interceptor';
import { LanguageService } from './core/i18n/language.service';
import { intlLocaleTag } from './core/i18n/supported-locales';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor, localeInterceptor])),
    provideTranslateHttpLoader({
      prefix: '/assets/i18n/',
      suffix: '.json',
    }),
    importProvidersFrom(
      TranslateModule.forRoot({
        defaultLanguage: 'en',
      }),
    ),
    LanguageService,
    {
      provide: LOCALE_ID,
      useFactory: (language: LanguageService) => intlLocaleTag(language.current()),
      deps: [LanguageService],
    },
  ],
};
