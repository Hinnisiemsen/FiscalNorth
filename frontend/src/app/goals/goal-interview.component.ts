import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AccountService, DepositAccount } from '../core/services/account.service';
import {
  GoalPlanResponse,
  GoalService,
  GoalType,
  RecommendedGoal,
} from '../core/services/goal.service';
import { EntitlementService } from '../core/services/entitlement.service';
import { PaywallBannerComponent } from '../shared/paywall-banner.component';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

const GOAL_TYPES: GoalType[] = [
  'EMERGENCY_FUND',
  'VACATION',
  'HOME',
  'DEBT_PAYOFF',
  'RETIREMENT',
  'OTHER',
];

@Component({
  selector: 'app-goal-interview',
  standalone: true,
  imports: [CommonModule, FormsModule, PaywallBannerComponent, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
  templateUrl: './goal-interview.component.html',
  styleUrl: './goal-interview.component.css',
})
export class GoalInterviewComponent implements OnInit {
  step = 1;
  readonly totalSteps = 6;
  sessionId: number | null = null;
  loading = false;
  planLoading = false;
  planError = '';

  selectedPriorities: GoalType[] = [];
  targets: Record<string, number> = {};
  targetDates: Record<string, string> = {};
  riskTolerance = 'moderate';
  savingsComfort = 'medium';
  monthlyWillingToSave = 200;
  accountLinks: Record<string, number | null> = {};

  plan: GoalPlanResponse | null = null;
  selectedGoals: Set<number> = new Set();
  editableGoals: RecommendedGoal[] = [];

  accounts: DepositAccount[] = [];
  readonly goalTypes = GOAL_TYPES;
  readonly riskOptions = ['conservative', 'moderate', 'aggressive'];
  readonly comfortOptions = ['low', 'medium', 'high'];

  constructor(
    private goalService: GoalService,
    private accountService: AccountService,
    private router: Router,
    private entitlementService: EntitlementService,
  ) {}

  get showPaywall(): boolean {
    return !this.entitlementService.hasFeature('AI_GOAL_PLANNER');
  }

  ngOnInit(): void {
    this.loading = true;
    this.accountService.getDepositAccounts().subscribe((accounts) => (this.accounts = accounts));
    this.goalService.startInterview().subscribe({
      next: (session) => {
        this.sessionId = session.id;
        this.loading = false;
      },
      error: () => (this.loading = false),
    });
  }

  togglePriority(type: GoalType): void {
    const idx = this.selectedPriorities.indexOf(type);
    if (idx >= 0) {
      this.selectedPriorities.splice(idx, 1);
    } else if (this.selectedPriorities.length < 3) {
      this.selectedPriorities.push(type);
    }
  }

  isPrioritySelected(type: GoalType): boolean {
    return this.selectedPriorities.includes(type);
  }

  canProceed(): boolean {
    switch (this.step) {
      case 1:
        return this.selectedPriorities.length > 0;
      case 2:
        return this.selectedPriorities.every((t) => this.targets[t] != null && this.targets[t] > 0);
      case 3:
        return this.monthlyWillingToSave > 0;
      default:
        return true;
    }
  }

  next(): void {
    if (!this.canProceed() || !this.sessionId) return;

    if (this.step < 4) {
      this.saveAnswersAndAdvance();
    } else if (this.step === 4) {
      this.saveAnswersAndAdvance();
    } else if (this.step === 5) {
      this.step = 6;
    }
  }

  back(): void {
    if (this.step > 1) {
      this.step--;
    }
  }

  generatePlan(): void {
    if (!this.sessionId) return;
    if (this.showPaywall) {
      this.planError = 'billing.paywall.goalPlanner';
      this.step = 5;
      return;
    }
    this.planLoading = true;
    this.planError = '';
    this.saveAnswers(() => {
      this.goalService.generatePlan(this.sessionId!).subscribe({
        next: (plan) => {
          this.plan = plan;
          this.editableGoals = plan.recommendedGoals.map((g) => ({ ...g }));
          this.selectedGoals = new Set(plan.recommendedGoals.map((_, i) => i));
          this.planLoading = false;
          this.step = 5;
        },
        error: (err) => {
          this.planError = this.entitlementService.isPremiumRequiredError(err)
            ? 'billing.paywall.goalPlanner'
            : 'goals.planError';
          this.planLoading = false;
        },
      });
    });
  }

  toggleGoalSelection(index: number): void {
    if (this.selectedGoals.has(index)) {
      this.selectedGoals.delete(index);
    } else {
      this.selectedGoals.add(index);
    }
  }

  applyPlan(): void {
    if (!this.sessionId || this.selectedGoals.size === 0) return;
    this.loading = true;
    const goals = this.editableGoals
      .filter((_, i) => this.selectedGoals.has(i))
      .map((g) => ({
        ...g,
        linkedAccountId: g.linkedAccountId ?? this.accountLinks[g.goalType] ?? undefined,
      }));
    this.goalService.applyPlan(this.sessionId, goals).subscribe({
      next: () => this.router.navigate(['/goals']),
      error: () => (this.loading = false),
    });
  }

  stepLabel(): string {
    return `goals.interview.step${this.step}`;
  }

  private saveAnswersAndAdvance(): void {
    this.saveAnswers(() => {
      if (this.step === 4) {
        this.generatePlan();
      } else {
        this.step++;
      }
    });
  }

  private saveAnswers(onDone: () => void): void {
    if (!this.sessionId) return;
    const answers = this.buildAnswers();
    this.goalService.saveInterviewAnswers(this.sessionId, answers).subscribe({
      next: () => onDone(),
      error: () => onDone(),
    });
  }

  private buildAnswers(): Record<string, unknown> {
    return {
      priorities: this.selectedPriorities,
      targets: this.targets,
      targetDates: this.targetDates,
      riskTolerance: this.riskTolerance,
      savingsComfort: this.savingsComfort,
      monthlyWillingToSave: this.monthlyWillingToSave,
      accountLinks: this.accountLinks,
    };
  }
}
