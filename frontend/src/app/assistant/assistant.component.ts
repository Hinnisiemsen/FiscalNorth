import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AiService, ChatResponse, ProposedAction } from '../core/services/ai.service';
import { EntitlementService } from '../core/services/entitlement.service';
import { BudgetService } from '../core/services/budget.service';
import { CategoryService } from '../core/services/category.service';
import { TransactionService } from '../core/services/transaction.service';
import { GoalService, GoalType } from '../core/services/goal.service';
import { ActionCardComponent } from './action-card.component';
import { PaywallBannerComponent } from '../shared/paywall-banner.component';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';
import { LanguageService } from '../core/i18n/language.service';

interface ChatMessage {
  role: 'user' | 'assistant';
  text: string;
  actions?: ProposedAction[];
  followUps?: string[];
}

interface MessageBlock {
  type: 'paragraph' | 'list';
  text?: string;
  items?: string[];
}

@Component({
  selector: 'app-assistant',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ActionCardComponent,
    PaywallBannerComponent,
    ...PAGE_HEADER_IMPORTS,
    ...TRANSLATE_IMPORTS,
  ],
  templateUrl: './assistant.component.html',
  styleUrl: './assistant.component.css',
})
export class AssistantComponent implements OnInit {
  private readonly lang = inject(LanguageService);
  private readonly entitlementService = inject(EntitlementService);

  readonly welcomeFollowUpKeys = [
    'assistant.welcomeExample1',
    'assistant.welcomeExample2',
    'assistant.welcomeExample3',
  ] as const;

  messages: ChatMessage[] = [];
  inputText = '';
  conversationId = '';
  loading = false;
  aiAvailable = false;
  setupHint = '';
  actionBusyIndex: number | null = null;
  actionResults = new Map<number, { text: string; error: boolean }>();

  private pendingQuery: string | null = null;
  private pendingAnalysis = false;

  get showPaywall(): boolean {
    return !this.entitlementService.hasFeature('AI_ASSISTANT');
  }

  constructor(
    private ai: AiService,
    private budgetService: BudgetService,
    private categoryService: CategoryService,
    private transactionService: TransactionService,
    private goalService: GoalService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      const q = params.get('q');
      const mode = params.get('mode');
      this.pendingQuery = q?.trim() || null;
      this.pendingAnalysis = mode === 'analysis';
      if (q || this.pendingAnalysis) {
        this.router.navigate([], {
          relativeTo: this.route,
          queryParams: { q: null, mode: null },
          queryParamsHandling: 'merge',
          replaceUrl: true,
        });
      }
      this.tryConsumePendingQuery();
    });

    this.ai.getStatus().subscribe({
      next: (s) => {
        this.aiAvailable = s.available;
        this.setupHint = s.available ? '' : s.message;
        this.tryConsumePendingQuery();
      },
      error: () => {
        this.aiAvailable = false;
        this.setupHint = this.lang.instant('assistant.unavailable');
      },
    });
  }

  private tryConsumePendingQuery(): void {
    if (!this.aiAvailable || this.loading) return;
    if (this.pendingAnalysis) {
      const query =
        'Erstelle eine Gesamtanalyse meiner Finanzen: Stärken, Risiken und 3 konkrete Empfehlungen auf Deutsch.';
      this.pendingAnalysis = false;
      this.pendingQuery = null;
      this.sendMessage(query);
      return;
    }
    if (this.pendingQuery) {
      const q = this.pendingQuery;
      this.pendingQuery = null;
      this.sendMessage(q);
    }
  }

  send(): void {
    const text = this.inputText.trim();
    if (!text || this.loading) return;
    this.sendMessage(text);
  }

  askFollowUp(question: string): void {
    const text = question.trim();
    if (!text || this.loading) return;
    this.sendMessage(text);
  }

  askWelcomeExample(key: string): void {
    this.askFollowUp(this.lang.instant(key));
  }

  showFollowUps(msgIndex: number): boolean {
    if (this.loading || msgIndex !== this.messages.length - 1) {
      return false;
    }
    const msg = this.messages[msgIndex];
    return msg.role === 'assistant' && (msg.followUps?.length ?? 0) > 0;
  }

  private sendMessage(text: string): void {
    this.messages.push({ role: 'user', text });
    this.inputText = '';
    this.loading = true;
    this.ai.chat(text, this.conversationId || undefined).subscribe({
      next: (res: ChatResponse) => {
        this.conversationId = res.conversationId;
        this.messages.push({
          role: 'assistant',
          text: res.reply,
          actions: res.proposedActions,
          followUps: res.followUpRecommendations ?? [],
        });
        this.loading = false;
      },
      error: (err) => {
        const msg = err?.error?.message || this.lang.instant('assistant.chatFailed');
        this.messages.push({ role: 'assistant', text: msg });
        this.loading = false;
      },
    });
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.send();
    }
  }

  confirmAction(msgIndex: number, actionIndex: number, action: ProposedAction): void {
    const key = msgIndex * 100 + actionIndex;
    this.actionBusyIndex = key;
    const done = (text: string, error = false) => {
      this.actionResults.set(key, { text, error });
      this.actionBusyIndex = null;
    };
    const p = action.payload;
    switch (action.type) {
      case 'CREATE_BUDGET':
        this.budgetService
          .createBudget({
            name: p['name'],
            limit: Number(p['limit']),
            startDate: p['startDate'],
            endDate: p['endDate'],
            categoryId: p['categoryId'] ? Number(p['categoryId']) : null,
          })
          .subscribe({
            next: () => done(this.lang.instant('assistant.actionBudgetCreated')),
            error: () => done(this.lang.instant('assistant.actionBudgetFailed'), true),
          });
        break;
      case 'CREATE_CATEGORY':
        this.categoryService
          .createCategory({
            name: String(p['name']),
            transactionType: String(p['transactionType']),
          })
          .subscribe({
            next: () => done(this.lang.instant('assistant.actionCategoryCreated')),
            error: () => done(this.lang.instant('assistant.actionCategoryFailed'), true),
          });
        break;
      case 'CREATE_TRANSACTION':
        this.transactionService
          .createPaymentTransaction({
            amount: Number(p['amount']),
            description: String(p['description']),
            transactionDate: String(p['transactionDate']),
            transactionType: String(p['transactionType']),
            category: p['categoryId'] ? { id: Number(p['categoryId']) } : null,
            contract: null,
            tags: null,
          })
          .subscribe({
            next: () => done(this.lang.instant('assistant.actionTransactionCreated')),
            error: () => done(this.lang.instant('assistant.actionTransactionFailed'), true),
          });
        break;
      case 'CREATE_GOAL':
        this.goalService
          .createGoal({
            name: String(p['name']),
            goalType: String(p['goalType']) as GoalType,
            targetAmount: Number(p['targetAmount']),
            targetDate: p['targetDate'] ? String(p['targetDate']) : undefined,
            linkedAccountId: p['linkedAccountId'] ? Number(p['linkedAccountId']) : undefined,
            monthlyContribution: p['monthlyContribution']
              ? Number(p['monthlyContribution'])
              : undefined,
          })
          .subscribe({
            next: () => done(this.lang.instant('assistant.actionGoalCreated')),
            error: () => done(this.lang.instant('assistant.actionGoalFailed'), true),
          });
        break;
    }
  }

  actionResult(msgIndex: number, actionIndex: number): string {
    return this.actionResults.get(msgIndex * 100 + actionIndex)?.text ?? '';
  }

  actionResultError(msgIndex: number, actionIndex: number): boolean {
    return this.actionResults.get(msgIndex * 100 + actionIndex)?.error ?? false;
  }

  isActionBusy(msgIndex: number, actionIndex: number): boolean {
    return this.actionBusyIndex === msgIndex * 100 + actionIndex;
  }

  formatMessage(text: string): MessageBlock[] {
    if (!text?.trim()) {
      return [{ type: 'paragraph', text: '' }];
    }
    const blocks: MessageBlock[] = [];
    const sections = text.split(/\n\n+/);
    for (const section of sections) {
      const trimmed = section.trim();
      if (!trimmed) continue;
      const lines = trimmed
        .split('\n')
        .map((l) => l.trim())
        .filter(Boolean);
      const isListLine = (l: string) => /^[-•*]\s+/.test(l) || /^\d+\.\s+/.test(l);
      const toListItem = (l: string) => l.replace(/^[-•*]\s+/, '').replace(/^\d+\.\s+/, '');

      if (lines.every(isListLine)) {
        blocks.push({ type: 'list', items: lines.map(toListItem) });
        continue;
      }

      let listBuffer: string[] = [];
      const flushList = () => {
        if (listBuffer.length) {
          blocks.push({ type: 'list', items: [...listBuffer] });
          listBuffer = [];
        }
      };

      for (const line of lines) {
        if (isListLine(line)) {
          listBuffer.push(toListItem(line));
        } else {
          flushList();
          blocks.push({ type: 'paragraph', text: line });
        }
      }
      flushList();
    }
    return blocks.length ? blocks : [{ type: 'paragraph', text: text.trim() }];
  }
}
