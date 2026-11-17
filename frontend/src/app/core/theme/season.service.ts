import { Injectable, signal } from '@angular/core';

export const SEASON_STORAGE_KEY = 'fn-season';
export type SeasonId = 'default' | 'yule';
export type GreetingKind = 'christmas' | 'newYear' | 'holidays' | 'timeOfDay';

/** Holiday window: 15 Nov through 6 Jan (Twelfth Night). */
export function isHolidayWindow(date: Date = new Date()): boolean {
  const month = date.getMonth();
  const day = date.getDate();
  if (month === 10 && day >= 15) return true;
  if (month === 11) return true;
  return month === 0 && day <= 6;
}

export function greetingKind(date: Date, yuleEnabled: boolean): GreetingKind {
  if (!yuleEnabled) return 'timeOfDay';
  const month = date.getMonth();
  const day = date.getDate();
  if (month === 11 && day >= 24 && day <= 26) return 'christmas';
  if ((month === 11 && day >= 31) || (month === 0 && day <= 6)) return 'newYear';
  return 'holidays';
}

@Injectable({ providedIn: 'root' })
export class SeasonService {
  readonly season = signal<SeasonId>('default');

  init(now: Date = new Date()): void {
    const stored = this.readStored();
    const next: SeasonId = stored ?? (isHolidayWindow(now) ? 'yule' : 'default');
    this.apply(next);
  }

  isYule(): boolean {
    return this.season() === 'yule';
  }

  toggle(): void {
    this.apply(this.isYule() ? 'default' : 'yule');
    this.persist(this.season());
  }

  setSeason(season: SeasonId): void {
    this.apply(season);
    this.persist(season);
  }

  private apply(season: SeasonId): void {
    this.season.set(season);
    if (typeof document === 'undefined') return;
    if (season === 'yule') {
      document.documentElement.setAttribute('data-season', 'yule');
    } else {
      document.documentElement.removeAttribute('data-season');
    }
  }

  private readStored(): SeasonId | null {
    if (typeof localStorage === 'undefined') return null;
    const value = localStorage.getItem(SEASON_STORAGE_KEY);
    return value === 'yule' || value === 'default' ? value : null;
  }

  private persist(season: SeasonId): void {
    if (typeof localStorage === 'undefined') return;
    localStorage.setItem(SEASON_STORAGE_KEY, season);
  }
}
