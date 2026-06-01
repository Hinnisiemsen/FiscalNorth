import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import { ActionCardComponent } from './action-card.component';
import { ProposedAction } from '../core/services/ai.service';

describe('ActionCardComponent', () => {
  let fixture: ComponentFixture<ActionCardComponent>;

  const budgetAction: ProposedAction = {
    type: 'CREATE_BUDGET',
    summary: 'Budget anlegen',
    payload: {
      name: 'Transport',
      limit: 120,
      startDate: '2026-06-01',
      endDate: '2026-06-30',
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActionCardComponent, TranslateModule.forRoot()],
    }).compileComponents();

    fixture = TestBed.createComponent(ActionCardComponent);
    fixture.componentRef.setInput('action', budgetAction);
    fixture.detectChanges();
  });

  it('should show budget title and detail rows', () => {
    const component = fixture.componentInstance;
    expect(component.actionTitle()).toBe('New budget');
    expect(component.detailRows().map((r) => r.label)).toEqual(
      jasmine.arrayContaining(['Name', 'Limit', 'Period']),
    );
  });

  it('should format limit with amount', () => {
    const limitRow = fixture.componentInstance.detailRows().find((r) => r.label === 'Limit');
    expect(limitRow?.value).toMatch(/120/);
  });
});
