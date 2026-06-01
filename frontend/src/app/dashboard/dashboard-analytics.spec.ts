import {
  buildConclusions,
  categoryShares,
  monthTotals,
  percentChange,
} from './dashboard-analytics';
import { MonthlyTrend } from '../core/services/insights.service';

describe('dashboard-analytics', () => {
  const trends: MonthlyTrend[] = [
    { year: 2026, month: 6, transactionType: 'Expense', amount: 500 },
    { year: 2026, month: 6, transactionType: 'Income', amount: 2000 },
    { year: 2026, month: 5, transactionType: 'Expense', amount: 400 },
    { year: 2026, month: 5, transactionType: 'Income', amount: 1800 },
  ];

  it('computes month totals', () => {
    const june = monthTotals(trends, 2026, 6);
    expect(june.expenses).toBe(500);
    expect(june.income).toBe(2000);
    expect(june.net).toBe(1500);
  });

  it('computes category shares', () => {
    const shares = categoryShares([
      { categoryName: 'Food', amount: 75 },
      { categoryName: 'Transport', amount: 25 },
    ]);
    expect(shares[0].sharePercent).toBe(75);
  });

  it('builds conclusions for negative disposable', () => {
    const conclusions = buildConclusions({
      current: monthTotals(trends, 2026, 6),
      previous: monthTotals(trends, 2026, 5),
      categoryShares: categoryShares([{ categoryName: 'Food', amount: 400 }]),
      budgets: [],
      totalBalance: 100,
      fixedCosts: 500,
      disposableIncome: -400,
      currentLabel: 'Juni 2026',
      t: (key) => key,
      locale: 'en',
    });
    expect(conclusions.some((c) => c.id === 'disposable-negative')).toBe(true);
  });

  it('calculates percent change', () => {
    expect(percentChange(500, 400)).toBe(25);
  });
});
