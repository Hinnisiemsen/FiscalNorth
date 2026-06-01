import {
  DEFAULT_LOCALE,
  intlLocaleTag,
  isAppLocale,
  LOCALE_STORAGE_KEY,
  resolveInitialLocale,
} from './supported-locales';

describe('supported-locales', () => {
  beforeEach(() => {
    localStorage.removeItem(LOCALE_STORAGE_KEY);
  });

  it('isAppLocale accepts supported codes', () => {
    expect(isAppLocale('en')).toBe(true);
    expect(isAppLocale('de')).toBe(true);
    expect(isAppLocale('fr')).toBe(true);
    expect(isAppLocale('es')).toBe(true);
    expect(isAppLocale('it')).toBe(false);
  });

  it('intlLocaleTag maps to BCP 47 tags', () => {
    expect(intlLocaleTag('de')).toBe('de-DE');
    expect(intlLocaleTag('fr')).toBe('fr-FR');
    expect(intlLocaleTag('es')).toBe('es-ES');
    expect(intlLocaleTag('en')).toBe('en-GB');
  });

  it('resolveInitialLocale prefers stored value', () => {
    localStorage.setItem(LOCALE_STORAGE_KEY, 'fr');
    expect(resolveInitialLocale()).toBe('fr');
  });

  it('resolveInitialLocale falls back to default', () => {
    expect(resolveInitialLocale()).toBe(DEFAULT_LOCALE);
  });
});
