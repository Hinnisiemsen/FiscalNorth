import { Component, inject, input, output } from '@angular/core';
import { ProposedAction } from '../core/services/ai.service';
import { LanguageService } from '../core/i18n/language.service';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

export interface ActionDetailRow {
    label: string;
    value: string;
}

@Component({
    selector: 'app-action-card',
    standalone: true,
    imports: [...TRANSLATE_IMPORTS],
    templateUrl: './action-card.component.html',
    styleUrl: './action-card.component.css',
})
export class ActionCardComponent {
    private readonly lang = inject(LanguageService);

    action = input.required<ProposedAction>();
    busy = input(false);
    result = input('');
    resultError = input(false);
    confirm = output<void>();
    dismiss = output<void>();

    actionTitle(): string {
        switch (this.action().type) {
            case 'CREATE_BUDGET':
                return this.lang.instant('actionCard.titleBudget');
            case 'CREATE_CATEGORY':
                return this.lang.instant('actionCard.titleCategory');
            case 'CREATE_TRANSACTION':
                return this.lang.instant('actionCard.titleTransaction');
            default:
                return this.lang.instant('actionCard.titleDefault');
        }
    }

    actionIcon(): string {
        switch (this.action().type) {
            case 'CREATE_BUDGET':
                return 'pie_chart';
            case 'CREATE_CATEGORY':
                return 'category';
            case 'CREATE_TRANSACTION':
                return 'receipt_long';
            default:
                return 'lightbulb';
        }
    }

    detailRows(): ActionDetailRow[] {
        const p = this.action().payload;
        switch (this.action().type) {
            case 'CREATE_BUDGET':
                return this.budgetRows(p);
            case 'CREATE_CATEGORY':
                return this.categoryRows(p);
            case 'CREATE_TRANSACTION':
                return this.transactionRows(p);
            default:
                return [];
        }
    }

    private budgetRows(p: Record<string, unknown>): ActionDetailRow[] {
        const rows: ActionDetailRow[] = [
            { label: this.lang.instant('actionCard.labelName'), value: String(p['name'] ?? '') },
            { label: this.lang.instant('actionCard.labelLimit'), value: this.formatMoney(p['limit']) },
            {
                label: this.lang.instant('actionCard.labelPeriod'),
                value: `${this.formatDate(p['startDate'])} – ${this.formatDate(p['endDate'])}`,
            },
        ];
        if (p['categoryId']) {
            rows.push({
                label: this.lang.instant('actionCard.labelCategoryId'),
                value: String(p['categoryId']),
            });
        }
        return rows;
    }

    private categoryRows(p: Record<string, unknown>): ActionDetailRow[] {
        const type = String(p['transactionType'] ?? '');
        return [
            { label: this.lang.instant('actionCard.labelName'), value: String(p['name'] ?? '') },
            { label: this.lang.instant('actionCard.labelType'), value: this.transactionTypeLabel(type) },
        ];
    }

    private transactionRows(p: Record<string, unknown>): ActionDetailRow[] {
        const type = String(p['transactionType'] ?? '');
        const rows: ActionDetailRow[] = [
            {
                label: this.lang.instant('actionCard.labelDescription'),
                value: String(p['description'] ?? ''),
            },
            { label: this.lang.instant('actionCard.labelAmount'), value: this.formatMoney(p['amount']) },
            {
                label: this.lang.instant('actionCard.labelDate'),
                value: this.formatDate(p['transactionDate']),
            },
            { label: this.lang.instant('actionCard.labelType'), value: this.transactionTypeLabel(type) },
        ];
        if (p['categoryId']) {
            rows.push({
                label: this.lang.instant('actionCard.labelCategoryId'),
                value: String(p['categoryId']),
            });
        }
        return rows;
    }

    private transactionTypeLabel(type: string): string {
        return type === 'Income'
            ? this.lang.instant('transactions.income')
            : this.lang.instant('transactions.expense');
    }

    private formatMoney(value: unknown): string {
        const n = Number(value);
        if (Number.isNaN(n)) return String(value ?? '');
        return new Intl.NumberFormat(this.lang.intlLocale(), { style: 'currency', currency: 'EUR' }).format(n);
    }

    private formatDate(value: unknown): string {
        if (!value) return '';
        const d = new Date(String(value));
        if (Number.isNaN(d.getTime())) return String(value);
        return d.toLocaleDateString(this.lang.intlLocale(), {
            day: 'numeric',
            month: 'short',
            year: 'numeric',
        });
    }
}
