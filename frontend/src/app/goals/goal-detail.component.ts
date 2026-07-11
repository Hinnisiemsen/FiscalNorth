import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AccountService, UnifiedAccount } from '../core/services/account.service';
import {
  GoalService,
  GoalStatus,
  GoalType,
  GoalWithProgress,
  UpdateGoalRequest,
} from '../core/services/goal.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

@Component({
  selector: 'app-goal-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
  templateUrl: './goal-detail.component.html',
  styleUrl: './goal-detail.component.css',
})
export class GoalDetailComponent implements OnInit {
  goal: GoalWithProgress | null = null;
  accounts: UnifiedAccount[] = [];
  loading = true;
  saving = false;
  editMode = false;

  form = {
    name: '',
    goalType: 'OTHER' as GoalType,
    targetAmount: 0,
    currentAmount: 0,
    targetDate: '',
    linkedAccountId: null as number | null,
    monthlyContribution: 0,
    status: 'ACTIVE' as GoalStatus,
  };

  readonly goalTypes: GoalType[] = [
    'EMERGENCY_FUND',
    'VACATION',
    'HOME',
    'DEBT_PAYOFF',
    'RETIREMENT',
    'OTHER',
  ];

  readonly statuses: GoalStatus[] = ['ACTIVE', 'COMPLETED', 'PAUSED'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private goalService: GoalService,
    private accountService: AccountService,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.accountService.getAllAccounts().subscribe((accounts) => (this.accounts = accounts));
    this.goalService.getGoal(id).subscribe({
      next: (goal) => {
        this.goal = goal;
        this.populateForm(goal);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.router.navigate(['/goals']);
      },
    });
  }

  getProgressPercent(): number {
    return this.goal ? Math.min(100, this.goal.progressPercent ?? 0) : 0;
  }

  getProgressBarClass(): string {
    if (!this.goal) return 'ok';
    if (this.goal.status === 'COMPLETED') return 'ok';
    if (!this.goal.onTrack) return 'over';
    const pct = this.getProgressPercent();
    if (pct >= 80) return 'warning';
    return 'ok';
  }

  toggleEdit(): void {
    if (this.goal) {
      this.populateForm(this.goal);
    }
    this.editMode = !this.editMode;
  }

  save(): void {
    if (!this.goal) return;
    this.saving = true;
    const request: UpdateGoalRequest = {
      name: this.form.name,
      goalType: this.form.goalType,
      targetAmount: this.form.targetAmount,
      currentAmount: this.form.currentAmount,
      targetDate: this.form.targetDate || undefined,
      linkedAccountId: this.form.linkedAccountId ?? undefined,
      monthlyContribution: this.form.monthlyContribution || undefined,
      status: this.form.status,
    };
    this.goalService.updateGoal(this.goal.id, request).subscribe({
      next: (updated) => {
        this.goal = updated;
        this.editMode = false;
        this.saving = false;
      },
      error: () => (this.saving = false),
    });
  }

  deleteGoal(): void {
    if (!this.goal || !confirm('Delete this goal?')) return;
    this.goalService.deleteGoal(this.goal.id).subscribe(() => this.router.navigate(['/goals']));
  }

  askAi(): void {
    if (!this.goal) return;
    this.router.navigate(['/assistant'], {
      queryParams: { q: `Wie stehe ich bei meinem Ziel "${this.goal.name}"?` },
    });
  }

  private populateForm(goal: GoalWithProgress): void {
    this.form = {
      name: goal.name,
      goalType: goal.goalType,
      targetAmount: goal.targetAmount,
      currentAmount: goal.currentAmount,
      targetDate: goal.targetDate ?? '',
      linkedAccountId: goal.linkedAccountId ?? null,
      monthlyContribution: goal.monthlyContribution ?? 0,
      status: goal.status,
    };
  }
}
