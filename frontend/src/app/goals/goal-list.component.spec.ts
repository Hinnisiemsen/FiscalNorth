import { GoalListComponent } from './goal-list.component';
import { GoalWithProgress } from '../core/services/goal.service';

describe('GoalListComponent helpers', () => {
  let component: GoalListComponent;

  beforeEach(() => {
    component = new GoalListComponent({} as never);
  });

  const sampleGoal = (overrides: Partial<GoalWithProgress> = {}): GoalWithProgress => ({
    id: 1,
    name: 'Test',
    goalType: 'VACATION',
    targetAmount: 1000,
    currentAmount: 200,
    status: 'ACTIVE',
    progressAmount: 200,
    progressPercent: 20,
    remainingAmount: 800,
    onTrack: true,
    ...overrides,
  });

  it('caps progress percent at 100', () => {
    expect(component.getProgressPercent(sampleGoal({ progressPercent: 150 }))).toBe(100);
  });

  it('returns warning class near completion', () => {
    expect(component.getProgressBarClass(sampleGoal({ progressPercent: 85, onTrack: true }))).toBe('warning');
  });

  it('returns over class when behind schedule', () => {
    expect(component.getProgressBarClass(sampleGoal({ onTrack: false }))).toBe('over');
  });
});
