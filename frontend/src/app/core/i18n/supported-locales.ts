export type AppLocale = 'en' | 'de' | 'fr' | 'es';

export interface LocaleOption {
  code: AppLocale;
  label: string;
}

export const SUPPORTED_LOCALES: LocaleOption[] = [
  { code: 'en', label: 'English' },
  { code: 'de', label: 'Deutsch' },
  { code: 'fr', label: 'Français' },
  { code: 'es', label: 'Español' },
];

export const DEFAULT_LOCALE: AppLocale = 'en';

export const LOCALE_STORAGE_KEY = 'fn.locale';

export function intlLocaleTag(locale: AppLocale): string {
  switch (locale) {
    case 'de':
      return 'de-DE';
    case 'fr':
      return 'fr-FR';
    case 'es':
      return 'es-ES';
    default:
      return 'en-GB';
  }
}

export function resolveInitialLocale(): AppLocale {
  if (typeof localStorage === 'undefined') {
    return DEFAULT_LOCALE;
  }
  const stored = localStorage.getItem(LOCALE_STORAGE_KEY);
  if (stored && isAppLocale(stored)) {
    return stored;
  }
  const nav = typeof navigator !== 'undefined' ? navigator.language.slice(0, 2) : 'en';
  return isAppLocale(nav) ? nav : DEFAULT_LOCALE;
}

export function isAppLocale(value: string): value is AppLocale {
  return value === 'en' || value === 'de' || value === 'fr' || value === 'es';
}
