import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export type GoalType =
  | 'EMERGENCY_FUND'
  | 'VACATION'
  | 'HOME'
  | 'DEBT_PAYOFF'
  | 'RETIREMENT'
  | 'OTHER';

export type GoalStatus = 'ACTIVE' | 'COMPLETED' | 'PAUSED';

export interface GoalWithProgress {
  id: number;
  name: string;
  goalType: GoalType;
  targetAmount: number;
  currentAmount: number;
  targetDate?: string;
  linkedAccountId?: number;
  linkedAccountName?: string;
  monthlyContribution?: number;
  status: GoalStatus;
  progressAmount: number;
  progressPercent: number;
  remainingAmount: number;
  daysRemaining?: number;
  onTrack: boolean;
}

export interface GoalOverview {
  totalGoals: number;
  activeGoals: number;
  completedCount: number;
  overallProgressPercent: number;
  totalRemaining: number;
}

export interface RecommendedGoal {
  name: string;
  goalType: GoalType;
  targetAmount: number;
  targetDate?: string;
  linkedAccountId?: number;
  monthlyContribution?: number;
  rationale?: string;
}

export interface GoalPlanResponse {
  summary: string;
  recommendedGoals: RecommendedGoal[];
  monthlySavingsTarget: number;
  insights: string[];
}

export interface InterviewSessionResponse {
  id: number;
  status: string;
  answersJson: string;
  plan?: GoalPlanResponse;
}

export interface CreateGoalRequest {
  name: string;
  goalType: GoalType;
  targetAmount: number;
  currentAmount?: number;
  targetDate?: string;
  linkedAccountId?: number;
  monthlyContribution?: number;
}

export interface UpdateGoalRequest {
  name?: string;
  goalType?: GoalType;
  targetAmount?: number;
  currentAmount?: number;
  targetDate?: string;
  linkedAccountId?: number;
  monthlyContribution?: number;
  status?: GoalStatus;
}

@Injectable({ providedIn: 'root' })
export class GoalService {
  constructor(private api: ApiService) {}

  getGoals(): Observable<GoalWithProgress[]> {
    return this.api.get<GoalWithProgress[]>('/goals');
  }

  getOverview(): Observable<GoalOverview> {
    return this.api.get<GoalOverview>('/goals/overview');
  }

  getGoal(id: number): Observable<GoalWithProgress> {
    return this.api.get<GoalWithProgress>(`/goals/${id}`);
  }

  createGoal(request: CreateGoalRequest): Observable<GoalWithProgress> {
    return this.api.post<GoalWithProgress>('/goals', request);
  }

  updateGoal(id: number, request: UpdateGoalRequest): Observable<GoalWithProgress> {
    return this.api.put<GoalWithProgress>(`/goals/${id}`, request);
  }

  deleteGoal(id: number): Observable<void> {
    return this.api.delete<void>(`/goals/${id}`);
  }

  startInterview(): Observable<InterviewSessionResponse> {
    return this.api.post<InterviewSessionResponse>('/goals/interview/start', {});
  }

  saveInterviewAnswers(sessionId: number, answers: Record<string, unknown>): Observable<InterviewSessionResponse> {
    return this.api.put<InterviewSessionResponse>(`/goals/interview/${sessionId}/answers`, { answers });
  }

  generatePlan(sessionId: number): Observable<GoalPlanResponse> {
    return this.api.post<GoalPlanResponse>(`/goals/interview/${sessionId}/generate-plan`, {});
  }

  applyPlan(sessionId: number, goals: RecommendedGoal[]): Observable<GoalWithProgress[]> {
    return this.api.post<GoalWithProgress[]>(`/goals/interview/${sessionId}/apply-plan`, { goals });
  }
}
