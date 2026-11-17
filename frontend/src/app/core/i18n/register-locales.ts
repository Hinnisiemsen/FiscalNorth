import { registerLocaleData } from '@angular/common';
import localeDe from '@angular/common/locales/de';
import localeEnGb from '@angular/common/locales/en-GB';
import localeEs from '@angular/common/locales/es';
import localeFr from '@angular/common/locales/fr';

let registered = false;

/** Angular currency/date pipes need locale data for de-DE / fr-FR / es-ES / en-GB. */
export function registerAppLocales(): void {
  if (registered) {
    return;
  }
  registerLocaleData(localeDe, 'de-DE');
  registerLocaleData(localeFr, 'fr-FR');
  registerLocaleData(localeEs, 'es-ES');
  registerLocaleData(localeEnGb, 'en-GB');
  registered = true;
}
