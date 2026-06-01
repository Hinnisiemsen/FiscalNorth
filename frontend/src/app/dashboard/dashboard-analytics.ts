import { BudgetWithUsage } from '../core/services/budget.service';
import { CategorySpending, MonthlyTrend } from '../core/services/insights.service';
import { intlLocaleTag, AppLocale } from '../core/i18n/supported-locales';

export type ConclusionSeverity = 'positive' | 'neutral' | 'warning' | 'critical';

export type TranslateFn = (key: string, params?: Record<string, string | number>) => string;

export interface DashboardKpi {
  label: string;
  value: number;
  format: 'currency' | 'percent' | 'number';
  trend?: {
    direction: 'up' | 'down' | 'flat';
    label: string;
    positiveIsGood: boolean;
  };
}

export interface DashboardConclusion {
  id: string;
  severity: ConclusionSeverity;
  icon: string;
  title: string;
  text: string;
}

export interface MonthTotals {
  expenses: number;
  income: number;
  net: number;
}

export function monthLabel(year: number, month: number, locale: AppLocale): string {
  const tag = intlLocaleTag(locale);
  const d = new Date(year, month - 1, 1);
  return new Intl.DateTimeFormat(tag, { month: 'long', year: 'numeric' }).format(d);
}

export function sumByType(
  trends: MonthlyTrend[],
  year: number,
  month: number,
  type: string,
): number {
  return trends
    .filter((t) => t.year === year && t.month === month && t.transactionType === type)
    .reduce((s, t) => s + t.amount, 0);
}

export function monthTotals(trends: MonthlyTrend[], year: number, month: number): MonthTotals {
  const expenses = sumByType(trends, year, month, 'Expense');
  const income = sumByType(trends, year, month, 'Income');
  return { expenses, income, net: income - expenses };
}

export function percentChange(current: number, previous: number): number | null {
  if (previous === 0) {
    return current === 0 ? 0 : null;
  }
  return ((current - previous) / previous) * 100;
}

export function formatTrend(
  current: number,
  previous: number,
  positiveIsGood: boolean,
  t: TranslateFn,
): DashboardKpi['trend'] {
  const pct = percentChange(current, previous);
  if (pct === null) {
    return { direction: 'flat', label: t('analytics.trendNoCompare'), positiveIsGood };
  }
  if (Math.abs(pct) < 1) {
    return { direction: 'flat', label: t('analytics.trendFlat'), positiveIsGood };
  }
  const direction = pct > 0 ? 'up' : 'down';
  const sign = pct > 0 ? '+' : '';
  return {
    direction,
    label: t('analytics.trendVsMonth', { sign, pct: pct.toFixed(0) }),
    positiveIsGood,
  };
}

export interface CategoryShare {
  categoryName: string;
  amount: number;
  sharePercent: number;
}

export function categoryShares(categories: CategorySpending[]): CategoryShare[] {
  const total = categories.reduce((s, c) => s + c.amount, 0);
  if (total <= 0) {
    return [];
  }
  return [...categories]
    .sort((a, b) => b.amount - a.amount)
    .map((c) => ({
      categoryName: c.categoryName,
      amount: c.amount,
      sharePercent: (c.amount / total) * 100,
    }));
}

export function buildKpis(
  current: MonthTotals,
  previous: MonthTotals,
  fixedCosts: number,
  totalBalance: number,
  t: TranslateFn,
): DashboardKpi[] {
  const savingsRate = current.income > 0 ? (current.net / current.income) * 100 : 0;
  const fixedShare = current.income > 0 ? (fixedCosts / current.income) * 100 : 0;
  const runwayMonths = fixedCosts > 0 ? totalBalance / fixedCosts : 0;

  return [
    {
      label: t('analytics.kpiExpenses'),
      value: current.expenses,
      format: 'currency',
      trend: formatTrend(current.expenses, previous.expenses, false, t),
    },
    {
      label: t('analytics.kpiIncome'),
      value: current.income,
      format: 'currency',
      trend: formatTrend(current.income, previous.income, true, t),
    },
    {
      label: t('analytics.kpiSavingsRate'),
      value: savingsRate,
      format: 'percent',
      trend: formatTrend(current.net, previous.net, true, t),
    },
    {
      label: t('analytics.kpiFixedShare'),
      value: fixedShare,
      format: 'percent',
    },
    {
      label: t('analytics.kpiRunway'),
      value: runwayMonths,
      format: 'number',
    },
  ];
}

export function buildConclusions(input: {
  current: MonthTotals;
  previous: MonthTotals;
  categoryShares: CategoryShare[];
  budgets: BudgetWithUsage[];
  totalBalance: number;
  fixedCosts: number;
  disposableIncome: number;
  currentLabel: string;
  t: TranslateFn;
  locale: AppLocale;
}): DashboardConclusion[] {
  const conclusions: DashboardConclusion[] = [];
  const {
    current,
    previous,
    categoryShares: shares,
    budgets,
    totalBalance,
    fixedCosts,
    disposableIncome,
    currentLabel,
    t,
    locale,
  } = input;

  if (disposableIncome < 0) {
    conclusions.push({
      id: 'disposable-negative',
      severity: 'critical',
      icon: 'warning',
      title: t('analytics.conclusion.liquidityPressureTitle'),
      text: t('analytics.conclusion.liquidityPressureText', {
        fixed: formatEuro(fixedCosts, locale),
        balance: formatEuro(totalBalance, locale),
      }),
    });
  } else if (fixedCosts > 0 && disposableIncome < fixedCosts) {
    conclusions.push({
      id: 'disposable-tight',
      severity: 'warning',
      icon: 'info',
      title: t('analytics.conclusion.bufferTightTitle'),
      text: t('analytics.conclusion.bufferTightText', {
        available: formatEuro(disposableIncome, locale),
      }),
    });
  } else {
    conclusions.push({
      id: 'disposable-ok',
      severity: 'positive',
      icon: 'check_circle',
      title: t('analytics.conclusion.liquidityOkTitle'),
      text: t('analytics.conclusion.liquidityOkText', {
        available: formatEuro(disposableIncome, locale),
      }),
    });
  }

  const expenseChange = percentChange(current.expenses, previous.expenses);
  if (expenseChange !== null && Math.abs(expenseChange) >= 8) {
    if (expenseChange > 0) {
      conclusions.push({
        id: 'expenses-up',
        severity: expenseChange >= 20 ? 'warning' : 'neutral',
        icon: 'trending_up',
        title: t('analytics.conclusion.expensesUpTitle'),
        text: t('analytics.conclusion.expensesUpText', {
          period: currentLabel,
          pct: expenseChange.toFixed(0),
          current: formatEuro(current.expenses, locale),
          previous: formatEuro(previous.expenses, locale),
        }),
      });
    } else {
      conclusions.push({
        id: 'expenses-down',
        severity: 'positive',
        icon: 'trending_down',
        title: t('analytics.conclusion.expensesDownTitle'),
        text: t('analytics.conclusion.expensesDownText', {
          pct: Math.abs(expenseChange).toFixed(0),
          current: formatEuro(current.expenses, locale),
          previous: formatEuro(previous.expenses, locale),
        }),
      });
    }
  }

  if (current.income > 0) {
    const rate = (current.net / current.income) * 100;
    if (current.net >= 0) {
      conclusions.push({
        id: 'savings-rate',
        severity: rate >= 15 ? 'positive' : 'neutral',
        icon: 'savings',
        title:
          current.net >= 0
            ? t('analytics.conclusion.monthlySurplusTitle')
            : t('analytics.conclusion.monthlySurplusTitleAlt'),
        text:
          rate >= 15
            ? t('analytics.conclusion.monthlySurplusGood', {
                rate: rate.toFixed(0),
                surplus: formatEuro(current.net, locale),
                period: currentLabel,
              })
            : t('analytics.conclusion.monthlySurplusNeutral', {
                period: currentLabel,
                surplus: formatEuro(current.net, locale),
                income: formatEuro(current.income, locale),
                rate: rate.toFixed(0),
              }),
      });
    } else {
      conclusions.push({
        id: 'deficit',
        severity: 'warning',
        icon: 'account_balance_wallet',
        title: t('analytics.conclusion.deficitTitle'),
        text: t('analytics.conclusion.deficitText', {
          period: currentLabel,
          amount: formatEuro(Math.abs(current.net), locale),
          rate: Math.abs(rate).toFixed(0),
        }),
      });
    }
  }

  if (shares.length > 0) {
    const top = shares[0];
    if (top.sharePercent >= 30) {
      conclusions.push({
        id: 'top-category',
        severity: top.sharePercent >= 45 ? 'warning' : 'neutral',
        icon: 'pie_chart',
        title: t('analytics.conclusion.focusTitle', { category: top.categoryName }),
        text: t('analytics.conclusion.focusText', {
          share: top.sharePercent.toFixed(0),
          amount: formatEuro(top.amount, locale),
          category: top.categoryName,
        }),
      });
    }
  }

  const activeBudgets = budgets.filter((b) => isBudgetActive(b));
  const critical = activeBudgets.filter((b) => budgetUsageRatio(b) >= 1);
  const warning = activeBudgets.filter((b) => {
    const r = budgetUsageRatio(b);
    return r >= 0.8 && r < 1;
  });

  if (critical.length > 0) {
    conclusions.push({
      id: 'budget-critical',
      severity: 'critical',
      icon: 'pie_chart',
      title:
        critical.length === 1
          ? t('analytics.conclusion.budgetExceededOne')
          : t('analytics.conclusion.budgetExceededMany', { count: critical.length }),
      text: t('analytics.conclusion.budgetExceededText', {
        names: critical.map((b) => b.name).join('", "'),
      }),
    });
  } else if (warning.length > 0) {
    conclusions.push({
      id: 'budget-warning',
      severity: 'warning',
      icon: 'pie_chart',
      title: t('analytics.conclusion.budgetsNearlyTitle'),
      text: t('analytics.conclusion.budgetsNearlyText', {
        count: warning.length,
        name: warning[0].name,
      }),
    });
  }

  return conclusions.slice(0, 6);
}

function budgetUsageRatio(b: BudgetWithUsage): number {
  if (!b.limit || b.limit <= 0) return 0;
  return b.spent / b.limit;
}

function isBudgetActive(b: BudgetWithUsage): boolean {
  const today = new Date();
  const start = new Date(b.startDate);
  const end = new Date(b.endDate);
  return today >= start && today <= end;
}

function formatEuro(value: number, locale: AppLocale): string {
  return new Intl.NumberFormat(intlLocaleTag(locale), {
    style: 'currency',
    currency: 'EUR',
  }).format(value);
}

/** @deprecated use monthLabel */
export const monthLabelDe = (year: number, month: number) => monthLabel(year, month, 'de');
