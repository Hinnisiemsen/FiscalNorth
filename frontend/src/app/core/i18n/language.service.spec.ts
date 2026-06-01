import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { importProvidersFrom } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';
import { LanguageService } from './language.service';
import { LOCALE_STORAGE_KEY } from './supported-locales';

describe('LanguageService', () => {
  beforeEach(() => {
    localStorage.removeItem(LOCALE_STORAGE_KEY);
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideTranslateHttpLoader({
          prefix: './assets/i18n/',
          suffix: '.json',
        }),
        importProvidersFrom(TranslateModule.forRoot()),
        LanguageService,
      ],
    });
  });

  it('persists selected locale', () => {
    const service = TestBed.inject(LanguageService);
    service.setLanguage('de');
    expect(localStorage.getItem(LOCALE_STORAGE_KEY)).toBe('de');
    expect(service.current()).toBe('de');
  });
});
