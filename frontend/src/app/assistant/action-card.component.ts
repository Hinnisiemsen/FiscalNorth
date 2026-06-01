import { Component, input, output } from '@angular/core';
import { ProposedAction } from '../core/services/ai.service';

@Component({
    selector: 'app-action-card',
    standalone: true,
    template: `
        <div class="action-proposal-card card-glass">
            <p class="action-summary">{{ action().summary }}</p>
            <span class="chip">{{ action().type }}</span>
            <div class="action-buttons">
                <button type="button" class="btn btn-secondary" (click)="dismiss.emit()">Dismiss</button>
                <button type="button" class="btn btn-primary" (click)="confirm.emit()" [disabled]="busy()">
                    {{ busy() ? 'Saving...' : 'Confirm' }}
                </button>
            </div>
            @if (result()) {
                <p class="action-result" [class.error]="resultError()">{{ result() }}</p>
            }
        </div>
    `,
})
export class ActionCardComponent {
    action = input.required<ProposedAction>();
    busy = input(false);
    result = input('');
    resultError = input(false);
    confirm = output<void>();
    dismiss = output<void>();
}
