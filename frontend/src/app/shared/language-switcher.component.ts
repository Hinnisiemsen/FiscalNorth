import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LanguageService } from '../core/i18n/language.service';
import { AppLocale, SUPPORTED_LOCALES } from '../core/i18n/supported-locales';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-language-switcher',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  template: `
    <label class="lang-switcher">
      <span class="sr-only">{{ 'layout.language' | translate }}</span>
      <select
        [value]="language.current()"
        (change)="onChange($event)"
        [attr.aria-label]="'layout.language' | translate"
      >
        @for (loc of locales; track loc.code) {
          <option [value]="loc.code">{{ loc.label }}</option>
        }
      </select>
    </label>
  `,
  styles: [
    `
      .lang-switcher select {
        font-size: 0.8125rem;
        padding: 0.35rem 0.5rem;
        border-radius: 8px;
        border: 1px solid var(--border-subtle, rgba(255, 255, 255, 0.12));
        background: var(--surface-glass, rgba(20, 24, 32, 0.6));
        color: inherit;
        max-width: 7.5rem;
      }
      .sr-only {
        position: absolute;
        width: 1px;
        height: 1px;
        padding: 0;
        margin: -1px;
        overflow: hidden;
        clip: rect(0, 0, 0, 0);
        border: 0;
      }
    `,
  ],
})
export class LanguageSwitcherComponent {
  readonly language = inject(LanguageService);
  readonly locales = SUPPORTED_LOCALES;

  onChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value as AppLocale;
    this.language.setLanguage(value);
  }
}
