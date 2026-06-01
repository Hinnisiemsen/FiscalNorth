import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { importProvidersFrom } from '@angular/core';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { localeInterceptor } from './locale.interceptor';
import { LanguageService } from './language.service';
import { LOCALE_STORAGE_KEY } from './supported-locales';

function httpLoaderFactory(http: HttpClient) {
    return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

describe('localeInterceptor', () => {
    let httpMock: HttpTestingController;

    beforeEach(() => {
        localStorage.removeItem(LOCALE_STORAGE_KEY);
        TestBed.configureTestingModule({
            providers: [
                provideHttpClient(withInterceptors([localeInterceptor])),
                provideHttpClientTesting(),
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
        httpMock = TestBed.inject(HttpTestingController);
        TestBed.inject(LanguageService).setLanguage('de');
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('adds Accept-Language header from LanguageService', () => {
        TestBed.inject(HttpClient).get('/api/test').subscribe();
        const req = httpMock.expectOne('/api/test');
        expect(req.request.headers.get('Accept-Language')).toBe('de');
        req.flush({});
    });
});
