import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { ActionCardComponent } from './action-card.component';
import { ProposedAction } from '../core/services/ai.service';
import { LOCALE_STORAGE_KEY } from '../core/i18n/supported-locales';

const ACTION_CARD_EN = {
  actionCard: {
    titleBudget: 'New budget',
    titleCategory: 'New category',
    titleTransaction: 'New transaction',
    titleDefault: 'Suggestion',
    labelName: 'Name',
    labelLimit: 'Limit',
    labelPeriod: 'Period',
    labelCategoryId: 'Category ID',
    labelDescription: 'Description',
    labelAmount: 'Amount',
    labelDate: 'Date',
    labelType: 'Type',
  },
};

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
    localStorage.removeItem(LOCALE_STORAGE_KEY);

    await TestBed.configureTestingModule({
      imports: [ActionCardComponent, TranslateModule.forRoot()],
    }).compileComponents();

    const translate = TestBed.inject(TranslateService);
    translate.setDefaultLang('en');
    translate.setTranslation('en', ACTION_CARD_EN);
    translate.use('en');

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
