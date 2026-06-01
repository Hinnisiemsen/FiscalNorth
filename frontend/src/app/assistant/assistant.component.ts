import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AiService, ChatResponse, ProposedAction } from '../core/services/ai.service';
import { BudgetService } from '../core/services/budget.service';
import { CategoryService } from '../core/services/category.service';
import { TransactionService } from '../core/services/transaction.service';
import { ActionCardComponent } from './action-card.component';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';

interface ChatMessage {
    role: 'user' | 'assistant';
    text: string;
    actions?: ProposedAction[];
}

@Component({
    selector: 'app-assistant',
    standalone: true,
    imports: [CommonModule, FormsModule, ActionCardComponent, ...PAGE_HEADER_IMPORTS],
    templateUrl: './assistant.component.html',
    styleUrl: './assistant.component.css',
})
export class AssistantComponent implements OnInit {
    messages: ChatMessage[] = [];
    inputText = '';
    conversationId = '';
    loading = false;
    aiAvailable = false;
    setupHint = '';
    actionBusyIndex: number | null = null;
    actionResults = new Map<number, { text: string; error: boolean }>();

    constructor(
        private ai: AiService,
        private budgetService: BudgetService,
        private categoryService: CategoryService,
        private transactionService: TransactionService
    ) {}

    ngOnInit(): void {
        this.ai.getStatus().subscribe({
            next: (s) => {
                this.aiAvailable = s.available;
                this.setupHint = s.available ? '' : s.message;
            },
            error: () => {
                this.aiAvailable = false;
                this.setupHint = 'Could not reach the assistant service.';
            },
        });
    }

    send(): void {
        const text = this.inputText.trim();
        if (!text || this.loading) return;
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
                });
                this.loading = false;
            },
            error: (err) => {
                const msg =
                    err?.error?.message ||
                    'Assistant unavailable. Set GEMINI_API_KEY on the backend.';
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
                        next: () => done('Budget created.'),
                        error: () => done('Failed to create budget.', true),
                    });
                break;
            case 'CREATE_CATEGORY':
                this.categoryService
                    .createCategory({
                        name: String(p['name']),
                        transactionType: String(p['transactionType']),
                    })
                    .subscribe({
                        next: () => done('Category created.'),
                        error: () => done('Failed to create category.', true),
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
                        next: () => done('Transaction created.'),
                        error: () => done('Failed to create transaction.', true),
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
}
