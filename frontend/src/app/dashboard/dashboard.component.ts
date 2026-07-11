import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountService } from '../core/services/account.service';
import { ContractService } from '../core/services/contract.service';
import { InsightsService, MonthlyTrend } from '../core/services/insights.service';
import { BudgetService, BudgetWithUsage } from '../core/services/budget.service';
import { GoalOverview, GoalService, GoalWithProgress } from '../core/services/goal.service';
import { PortfolioOverview, PortfolioService } from '../core/services/portfolio.service';
import { UserService } from '../core/services/user.service';
import { AiService } from '../core/services/ai.service';
import { forkJoin } from 'rxjs';
import { AppNotification, NotificationService } from '../core/services/notification.service';
import { RouterLink } from '@angular/router';
import {
  buildConclusions,
  buildKpis,
  categoryShares,
  DashboardConclusion,
  DashboardKpi,
  CategoryShare,
  monthLabel,
  monthTotals,
  MonthTotals,
  percentChange,
} from './dashboard-analytics';
import { ASK_CONTEXTS, DashboardPanel } from './dashboard-ask-prompts';
import { FiscalNorthAskBarComponent } from '../shared/fiscal-north-ask-bar.component';
import { LanguageService } from '../core/i18n/language.service';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

export interface PanelAiState {
  loading: boolean;
  error: string;
  text: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, FiscalNorthAskBarComponent, ...TRANSLATE_IMPORTS],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit {
  totalBalance = 0;
  portfolioValue = 0;
  netWorth = 0;
  householdBudgetSpent = 0;
  householdBudgetLimit = 0;
  fixedCosts = 0;
  disposableIncome = 0;
  userName = '';
  greeting = '';
  loading = true;
  aiAvailable = false;

  currentYear = new Date().getFullYear();
  currentMonth = new Date().getMonth() + 1;
  periodLabel = '';

  categoryBreakdown: CategoryShare[] = [];
  monthlyTrends: MonthlyTrend[] = [];
  budgets: BudgetWithUsage[] = [];
  goals: GoalWithProgress[] = [];
  goalOverview: GoalOverview | null = null;

  currentTotals: MonthTotals = { expenses: 0, income: 0, net: 0 };
  previousTotals: MonthTotals = { expenses: 0, income: 0, net: 0 };

  kpis: DashboardKpi[] = [];
  conclusions: DashboardConclusion[] = [];
  recentNotifications: AppNotification[] = [];

  heroConclusion = '';
  expenseTrendLabel = '';

  readonly generalAsk = ASK_CONTEXTS.general;

  private expandedPanels = new Set<DashboardPanel>();
  private panelAiState: Partial<Record<DashboardPanel, PanelAiState>> = {};

  constructor(
    private accountService: AccountService,
    private contractService: ContractService,
    private insightsService: InsightsService,
    private budgetService: BudgetService,
    private goalService: GoalService,
    private portfolioService: PortfolioService,
    private notificationService: NotificationService,
    private userService: UserService,
    private ai: AiService,
    private language: LanguageService,
  ) {}

  ngOnInit(): void {
    this.periodLabel = monthLabel(this.currentYear, this.currentMonth, this.language.current());

    const prevMonth = this.currentMonth === 1 ? 12 : this.currentMonth - 1;
    const prevYear = this.currentMonth === 1 ? this.currentYear - 1 : this.currentYear;

    this.ai.getStatus().subscribe({
      next: (s) => (this.aiAvailable = s.available),
      error: () => (this.aiAvailable = false),
    });

    forkJoin({
      user: this.userService.getCurrentUser(),
      accounts: this.accountService.getAllAccounts(),
      contracts: this.contractService.getContracts(),
      currentInsights: this.insightsService.getInsights(this.currentYear, this.currentMonth),
      previousInsights: this.insightsService.getInsights(prevYear, prevMonth),
      budgets: this.budgetService.getBudgetsWithUsage(),
      goals: this.goalService.getGoals(),
      goalOverview: this.goalService.getOverview(),
      portfolio: this.portfolioService.getPortfolio(),
      notifications: this.notificationService.list(true),
    }).subscribe({
      next: ({
        user,
        accounts,
        contracts,
        currentInsights,
        previousInsights,
        budgets,
        goals,
        goalOverview,
        portfolio,
        notifications,
      }) => {
        this.userName = user.userName?.trim() || this.language.instant('dashboard.defaultUser');
        this.greeting = this.buildGreeting(this.userName);

        this.recentNotifications = notifications.slice(0, 5);
        this.totalBalance = accounts.reduce((sum, acc) => sum + acc.balance, 0);
        this.portfolioValue = portfolio?.totalValue ?? 0;
        this.netWorth = this.totalBalance + this.portfolioValue;
        this.fixedCosts = contracts.reduce((sum, c) => sum + c.amount, 0);
        this.disposableIncome = this.totalBalance - this.fixedCosts;

        const active = budgets.filter(
          (b) => !b.endDate || new Date(b.endDate) >= new Date(new Date().toDateString()),
        );
        this.householdBudgetSpent = active.reduce((sum, b) => sum + (b.spent ?? 0), 0);
        this.householdBudgetLimit = active.reduce((sum, b) => sum + (b.limit ?? 0), 0);

        this.categoryBreakdown = categoryShares(currentInsights.spendingByCategory);
        this.monthlyTrends = [...currentInsights.monthlyTrends, ...previousInsights.monthlyTrends];
        this.budgets = budgets;
        this.goals = goals;
        this.goalOverview = goalOverview;

        this.currentTotals = monthTotals(this.monthlyTrends, this.currentYear, this.currentMonth);
        this.previousTotals = monthTotals(this.monthlyTrends, prevYear, prevMonth);

        const expenseChange = percentChange(
          this.currentTotals.expenses,
          this.previousTotals.expenses,
        );
        const t = (key: string, params?: Record<string, string | number>) =>
          this.language.instant(key, params);
        if (expenseChange === null) {
          this.expenseTrendLabel = '';
        } else if (Math.abs(expenseChange) < 1) {
          this.expenseTrendLabel = t('dashboard.expenseTrendFlat');
        } else {
          this.expenseTrendLabel = t('dashboard.expenseTrend', {
            sign: expenseChange > 0 ? '+' : '',
            pct: expenseChange.toFixed(0),
          });
        }

        this.kpis = buildKpis(
          this.currentTotals,
          this.previousTotals,
          this.fixedCosts,
          this.totalBalance,
          t,
        );
        this.conclusions = buildConclusions({
          current: this.currentTotals,
          previous: this.previousTotals,
          categoryShares: this.categoryBreakdown,
          budgets: this.budgets,
          totalBalance: this.totalBalance,
          fixedCosts: this.fixedCosts,
          disposableIncome: this.disposableIncome,
          currentLabel: this.periodLabel,
          t,
          locale: this.language.current(),
        });
        this.heroConclusion = this.shorten(this.primaryConclusion()?.text ?? '', 120);
        this.loading = false;
      },
      error: () => {
        this.userName = this.language.instant('dashboard.defaultUser');
        this.greeting = this.buildGreeting(this.userName);
        this.loading = false;
      },
    });
  }

  toggle(panel: DashboardPanel): void {
    if (this.expandedPanels.has(panel)) {
      this.expandedPanels.delete(panel);
      return;
    }
    this.expandedPanels.add(panel);
    this.loadPanelAnalysis(panel);
  }

  isExpanded(panel: DashboardPanel): boolean {
    return this.expandedPanels.has(panel);
  }

  panelAi(panel: DashboardPanel): PanelAiState {
    return this.panelAiState[panel] ?? { loading: false, error: '', text: '' };
  }

  private loadPanelAnalysis(panel: DashboardPanel): void {
    if (!this.aiAvailable) {
      this.panelAiState[panel] = {
        loading: false,
        error: this.language.instant('dashboard.errors.aiUnavailable'),
        text: '',
      };
      return;
    }

    this.panelAiState[panel] = { loading: true, error: '', text: '' };
    const cfg = ASK_CONTEXTS[panel];
    const query = cfg.buildAnalysisQuery(this.periodLabel);

    this.ai.chat(query).subscribe({
      next: (res) => {
        this.panelAiState[panel] = {
          loading: false,
          error: '',
          text: res.reply?.trim() || this.language.instant('dashboard.errors.noAnalysis'),
        };
      },
      error: (err) => {
        this.panelAiState[panel] = {
          loading: false,
          error: err?.error?.message || this.language.instant('dashboard.errors.analysisFailed'),
          text: '',
        };
      },
    });
  }

  primaryConclusion(): DashboardConclusion | undefined {
    return (
      this.conclusions.find((c) => c.severity === 'critical') ??
      this.conclusions.find((c) => c.severity === 'warning') ??
      this.conclusions.find((c) => c.id === 'savings-rate') ??
      this.conclusions[0]
    );
  }

  fazitTeaser(): string {
    return this.primaryConclusion()?.title ?? this.language.instant('dashboard.teaserNoIssues');
  }

  monatTeaser(): string {
    return this.language.instant('dashboard.teaserMonth', {
      net: this.formatEuro(this.currentTotals.net),
      expenses: this.formatEuro(this.currentTotals.expenses),
    });
  }

  kategorienTeaser(): string {
    const top = this.categoryBreakdown[0];
    if (!top) return this.language.instant('dashboard.teaserNoExpenses');
    return `${top.categoryName} (${top.sharePercent.toFixed(0)} %)`;
  }

  verlaufTeaser(): string {
    const change = percentChange(this.currentTotals.expenses, this.previousTotals.expenses);
    if (change === null) return this.language.instant('dashboard.teaserTrendCompare');
    if (Math.abs(change) < 1) return this.language.instant('dashboard.teaserTrendStable');
    if (change > 0) {
      return this.language.instant('dashboard.teaserTrendMore', {
        pct: Math.abs(change).toFixed(0),
      });
    }
    return this.language.instant('dashboard.teaserTrendLess', { pct: Math.abs(change).toFixed(0) });
  }

  budgetsTeaser(): string {
    const active = this.activeBudgets();
    if (active.length === 0) return this.language.instant('dashboard.teaserNoBudgets');
    const worst = active[0];
    const pct = this.budgetUsagePercent(worst);
    if (pct >= 100) {
      return this.language.instant('dashboard.teaserBudgetExceeded', { name: worst.name });
    }
    if (pct >= 80) {
      return this.language.instant('dashboard.teaserBudgetAt', {
        name: worst.name,
        pct: pct.toFixed(0),
      });
    }
    return this.language.instant('dashboard.teaserBudgetsOnTrack', { count: active.length });
  }

  goalsTeaser(): string {
    if (!this.goalOverview || this.goalOverview.totalGoals === 0) {
      return this.language.instant('dashboard.teaserNoGoals');
    }
    return this.language.instant('dashboard.teaserGoals', {
      pct: this.goalOverview.overallProgressPercent.toFixed(0),
      count: this.goalOverview.activeGoals,
    });
  }

  topGoals(): GoalWithProgress[] {
    return [...this.goals]
      .filter((g) => g.status === 'ACTIVE')
      .sort((a, b) => b.progressPercent - a.progressPercent)
      .slice(0, 3);
  }

  goalProgressPercent(g: GoalWithProgress): number {
    return Math.min(100, g.progressPercent ?? 0);
  }

  goalStatusClass(g: GoalWithProgress): string {
    if (g.status === 'COMPLETED') return 'ok';
    if (!g.onTrack) return 'critical';
    const pct = this.goalProgressPercent(g);
    if (pct >= 80) return 'warning';
    return 'ok';
  }

  hinweiseTeaser(): string {
    const n = this.recentNotifications.length;
    return n === 1
      ? this.language.instant('dashboard.teaserOneHint')
      : this.language.instant('dashboard.teaserHints', { count: n });
  }

  getBarWidth(sharePercent: number): number {
    return Math.min(100, Math.max(0, sharePercent));
  }

  budgetUsagePercent(b: BudgetWithUsage): number {
    if (!b.limit || b.limit <= 0) return 0;
    return Math.min(100, (b.spent / b.limit) * 100);
  }

  budgetStatusClass(b: BudgetWithUsage): string {
    const pct = this.budgetUsagePercent(b);
    if (pct >= 100) return 'critical';
    if (pct >= 80) return 'warning';
    return 'ok';
  }

  activeBudgets(): BudgetWithUsage[] {
    const today = new Date();
    return this.budgets
      .filter((b) => {
        const start = new Date(b.startDate);
        const end = new Date(b.endDate);
        return today >= start && today <= end;
      })
      .sort((a, b) => this.budgetUsagePercent(b) - this.budgetUsagePercent(a));
  }

  conclusionClass(severity: DashboardConclusion['severity']): string {
    return severity;
  }

  trendClass(kpi: DashboardKpi): string {
    if (!kpi.trend) return '';
    const good =
      (kpi.trend.direction === 'up' && kpi.trend.positiveIsGood) ||
      (kpi.trend.direction === 'down' && !kpi.trend.positiveIsGood) ||
      kpi.trend.direction === 'flat';
    return good ? 'trend-good' : 'trend-bad';
  }

  formatKpiValue(kpi: DashboardKpi): string | number {
    if (kpi.format === 'percent') {
      return `${kpi.value.toFixed(0)} %`;
    }
    return kpi.value.toFixed(1);
  }

  isCurrencyKpi(kpi: DashboardKpi): boolean {
    return kpi.format === 'currency';
  }

  get uniqueMonths(): { year: number; month: number }[] {
    const seen = new Set<string>();
    return this.monthlyTrends
      .map((t) => ({ year: t.year, month: t.month }))
      .filter((m) => {
        const k = `${m.year}-${m.month}`;
        if (seen.has(k)) return false;
        seen.add(k);
        return true;
      })
      .sort((a, b) => (a.year !== b.year ? a.year - b.year : a.month - b.month))
      .slice(-4);
  }

  getMonthLabel(year: number, month: number): string {
    return monthLabel(year, month, this.language.current());
  }

  getMonthNet(year: number, month: number): number {
    return monthTotals(this.monthlyTrends, year, month).net;
  }

  splitAiParagraphs(text: string): string[] {
    return text
      .split(/\n\n+/)
      .map((p) => p.trim())
      .filter(Boolean);
  }

  private buildGreeting(name: string): string {
    const h = new Date().getHours();
    const first = name.split(/\s+/)[0];
    if (h < 12) {
      return this.language.instant('dashboard.greetingMorning', { name: first });
    }
    if (h < 18) {
      return this.language.instant('dashboard.greetingAfternoon', { name: first });
    }
    return this.language.instant('dashboard.greetingEvening', { name: first });
  }

  private shorten(text: string, max: number): string {
    if (text.length <= max) return text;
    return text.slice(0, max).trimEnd() + '…';
  }

  private formatEuro(value: number): string {
    return new Intl.NumberFormat(this.language.intlLocale(), {
      style: 'currency',
      currency: 'EUR',
      maximumFractionDigits: 0,
    }).format(value);
  }
}
