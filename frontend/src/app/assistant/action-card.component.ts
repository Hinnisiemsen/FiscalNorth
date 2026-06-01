import { Component, input, output } from '@angular/core';
import { ProposedAction } from '../core/services/ai.service';

export interface ActionDetailRow {
    label: string;
    value: string;
}

@Component({
    selector: 'app-action-card',
    standalone: true,
    imports: [],
    templateUrl: './action-card.component.html',
    styleUrl: './action-card.component.css',
})
export class ActionCardComponent {
    action = input.required<ProposedAction>();
    busy = input(false);
    result = input('');
    resultError = input(false);
    confirm = output<void>();
    dismiss = output<void>();

    actionTitle(): string {
        switch (this.action().type) {
            case 'CREATE_BUDGET':
                return 'Neues Budget';
            case 'CREATE_CATEGORY':
                return 'Neue Kategorie';
            case 'CREATE_TRANSACTION':
                return 'Neue Buchung';
            default:
                return 'Vorschlag';
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
            { label: 'Bezeichnung', value: String(p['name'] ?? '') },
            { label: 'Limit', value: this.formatMoney(p['limit']) },
            { label: 'Zeitraum', value: `${this.formatDate(p['startDate'])} – ${this.formatDate(p['endDate'])}` },
        ];
        if (p['categoryId']) {
            rows.push({ label: 'Kategorie-ID', value: String(p['categoryId']) });
        }
        return rows;
    }

    private categoryRows(p: Record<string, unknown>): ActionDetailRow[] {
        const type = String(p['transactionType'] ?? '');
        return [
            { label: 'Name', value: String(p['name'] ?? '') },
            { label: 'Art', value: type === 'Income' ? 'Einnahme' : 'Ausgabe' },
        ];
    }

    private transactionRows(p: Record<string, unknown>): ActionDetailRow[] {
        const type = String(p['transactionType'] ?? '');
        const rows: ActionDetailRow[] = [
            { label: 'Beschreibung', value: String(p['description'] ?? '') },
            { label: 'Betrag', value: this.formatMoney(p['amount']) },
            { label: 'Datum', value: this.formatDate(p['transactionDate']) },
            { label: 'Art', value: type === 'Income' ? 'Einnahme' : 'Ausgabe' },
        ];
        if (p['categoryId']) {
            rows.push({ label: 'Kategorie-ID', value: String(p['categoryId']) });
        }
        return rows;
    }

    private formatMoney(value: unknown): string {
        const n = Number(value);
        if (Number.isNaN(n)) return String(value ?? '');
        return new Intl.NumberFormat('de-DE', { style: 'currency', currency: 'EUR' }).format(n);
    }

    private formatDate(value: unknown): string {
        if (!value) return '';
        const d = new Date(String(value));
        if (Number.isNaN(d.getTime())) return String(value);
        return d.toLocaleDateString('de-DE', { day: 'numeric', month: 'short', year: 'numeric' });
    }
}
