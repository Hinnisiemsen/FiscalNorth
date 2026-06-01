import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TransactionService, CsvImportResult } from '../core/services/transaction.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';
import { LanguageService } from '../core/i18n/language.service';

type BankPreset = 'SPARKASSE' | 'ING';

@Component({
    selector: 'app-import',
    standalone: true,
    imports: [CommonModule, RouterLink, FormsModule, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
    templateUrl: './import.component.html',
    styleUrl: './import.component.css'
})
export class ImportComponent {
    private readonly lang = inject(LanguageService);

    selectedPreset: BankPreset = 'SPARKASSE';
    selectedFile: File | null = null;
    isImporting = false;
    result: CsvImportResult | null = null;
    error: string | null = null;

    readonly presets = [
        { value: 'SPARKASSE' as BankPreset, labelKey: 'import.presetSparkasse' },
        { value: 'ING' as BankPreset, labelKey: 'import.presetIng' },
    ];

    constructor(private transactionService: TransactionService) { }

    presetLabel(key: string): string {
        return this.lang.instant(key);
    }

    onFileSelected(event: Event) {
        const input = event.target as HTMLInputElement;
        if (input.files?.length) {
            this.selectedFile = input.files[0];
            this.result = null;
            this.error = null;
        }
    }

    importCsv() {
        if (!this.selectedFile) {
            this.error = this.lang.instant('import.selectFileError');
            return;
        }
        this.isImporting = true;
        this.result = null;
        this.error = null;

        this.transactionService.importCsv(this.selectedFile, this.selectedPreset).subscribe({
            next: (res) => {
                this.result = res;
                this.isImporting = false;
            },
            error: (err) => {
                this.error = err.error?.message || err.message || this.lang.instant('import.importFailed');
                this.isImporting = false;
            }
        });
    }

    reset() {
        this.selectedFile = null;
        this.result = null;
        this.error = null;
        const input = document.getElementById('csv-file') as HTMLInputElement;
        if (input) input.value = '';
    }
}
