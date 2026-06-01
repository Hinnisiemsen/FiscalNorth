import { Component, input, model } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

@Component({
    selector: 'app-fiscal-north-ask-bar',
    standalone: true,
    imports: [CommonModule, FormsModule, ...TRANSLATE_IMPORTS],
    templateUrl: './fiscal-north-ask-bar.component.html',
    styleUrl: './fiscal-north-ask-bar.component.css',
})
export class FiscalNorthAskBarComponent {
    /** compact = inline in panels; hero = top prominent bar */
    variant = input<'hero' | 'compact' | 'inline'>('compact');
    placeholder = input<string | undefined>(undefined);
    suggestions = input<string[]>([]);
    showAnalysisChip = input(true);

    question = model('');

    constructor(private router: Router) {}

    submit(): void {
        const text = this.question().trim();
        if (!text) return;
        this.goToAssistant(text);
        this.question.set('');
    }

    ask(text: string): void {
        this.goToAssistant(text);
    }

    startGeneralAnalysis(): void {
        this.goToAssistant('__analysis__');
    }

    onKeydown(event: KeyboardEvent): void {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            this.submit();
        }
    }

    private goToAssistant(queryOrToken: string): void {
        const q = queryOrToken === '__analysis__' ? '' : queryOrToken;
        const params: Record<string, string> = {};
        if (q) {
            params['q'] = q;
        }
        if (queryOrToken === '__analysis__') {
            params['mode'] = 'analysis';
        }
        this.router.navigate(['/assistant'], { queryParams: params });
    }
}
