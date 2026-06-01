import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { importProvidersFrom } from '@angular/core';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { HttpClient } from '@angular/common/http';
import { LanguageService } from './language.service';
import { LOCALE_STORAGE_KEY } from './supported-locales';

function httpLoaderFactory(http: HttpClient) {
    return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

describe('LanguageService', () => {
    beforeEach(() => {
        localStorage.removeItem(LOCALE_STORAGE_KEY);
        TestBed.configureTestingModule({
            providers: [
                provideHttpClient(),
                importProvidersFrom(
                    TranslateModule.forRoot({
                        loader: {
                            provide: TranslateLoader,
                            useFactory: httpLoaderFactory,
                            deps: [HttpClient],
                        },
                    })
                ),
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
