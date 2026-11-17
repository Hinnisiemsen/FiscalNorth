import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import { registerAppLocales } from './app/core/i18n/register-locales';

registerAppLocales();
bootstrapApplication(App, appConfig).catch((err) => console.error(err));
