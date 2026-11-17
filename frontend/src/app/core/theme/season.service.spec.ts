import { greetingKind, isHolidayWindow } from './season.service';

describe('holiday season helpers', () => {
  it('treats mid-November through Twelfth Night as the holiday window', () => {
    expect(isHolidayWindow(new Date(2026, 10, 14))).toBe(false);
    expect(isHolidayWindow(new Date(2026, 10, 15))).toBe(true);
    expect(isHolidayWindow(new Date(2026, 11, 24))).toBe(true);
    expect(isHolidayWindow(new Date(2027, 0, 6))).toBe(true);
    expect(isHolidayWindow(new Date(2027, 0, 7))).toBe(false);
    expect(isHolidayWindow(new Date(2026, 5, 1))).toBe(false);
  });

  it('picks Christmas, New Year, and generic holiday greetings', () => {
    expect(greetingKind(new Date(2026, 11, 25), true)).toBe('christmas');
    expect(greetingKind(new Date(2027, 0, 1), true)).toBe('newYear');
    expect(greetingKind(new Date(2026, 10, 17), true)).toBe('holidays');
    expect(greetingKind(new Date(2026, 11, 25), false)).toBe('timeOfDay');
  });
});
