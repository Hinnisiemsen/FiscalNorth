import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TransactionService, CsvImportResult } from '../core/services/transaction.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';

type BankPreset = 'SPARKASSE' | 'ING';

@Component({
    selector: 'app-import',
    standalone: true,
    imports: [CommonModule, RouterLink, FormsModule, ...PAGE_HEADER_IMPORTS],
    templateUrl: './import.component.html',
    styleUrl: './import.component.css'
})
export class ImportComponent {
    selectedPreset: BankPreset = 'SPARKASSE';
    selectedFile: File | null = null;
    isImporting = false;
    result: CsvImportResult | null = null;
    error: string | null = null;

    presets = [
        { value: 'SPARKASSE' as BankPreset, label: 'Sparkasse (Semicolon, German format)' },
        { value: 'ING' as BankPreset, label: 'ING-DiBa (Semicolon, German format)' }
    ];

    constructor(private transactionService: TransactionService) { }

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
            this.error = 'Please select a CSV file.';
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
                this.error = err.error?.message || err.message || 'Import failed';
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
