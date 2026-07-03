import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { GoalOverview, GoalService, GoalWithProgress } from '../core/services/goal.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-goal-list',
  standalone: true,
  imports: [CommonModule, RouterLink, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
  templateUrl: './goal-list.component.html',
  styleUrl: './goal-list.component.css',
})
export class GoalListComponent implements OnInit {
  goals: GoalWithProgress[] = [];
  overview: GoalOverview | null = null;
  loading = true;

  constructor(private goalService: GoalService) {}

  ngOnInit(): void {
    forkJoin({
      goals: this.goalService.getGoals(),
      overview: this.goalService.getOverview(),
    }).subscribe({
      next: ({ goals, overview }) => {
        this.goals = goals;
        this.overview = overview;
        this.loading = false;
      },
      error: () => (this.loading = false),
    });
  }

  getProgressPercent(goal: GoalWithProgress): number {
    return Math.min(100, goal.progressPercent ?? 0);
  }

  getProgressBarClass(goal: GoalWithProgress): string {
    if (goal.status === 'COMPLETED') return 'ok';
    if (!goal.onTrack) return 'over';
    const pct = this.getProgressPercent(goal);
    if (pct >= 80) return 'warning';
    return 'ok';
  }

  goalTypeKey(goalType: string): string {
    return `goals.types.${goalType}`;
  }
}
