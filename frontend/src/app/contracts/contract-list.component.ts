import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ContractService, Contract } from '../core/services/contract.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';
import { LanguageService } from '../core/i18n/language.service';

@Component({
  selector: 'app-contract-list',
  standalone: true,
  imports: [CommonModule, RouterLink, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
  templateUrl: './contract-list.component.html',
  styleUrl: './contract-list.component.css'
})
export class ContractListComponent implements OnInit {
  private readonly lang = inject(LanguageService);

  contracts: Contract[] = [];
  isAnalyzing = false;

  constructor(private contractService: ContractService) { }

  ngOnInit() {
    this.loadContracts();
  }

  loadContracts() {
    this.contractService.getContracts().subscribe(data => {
      this.contracts = data;
    });
  }

  intervalLabel(interval: string): string {
    switch (interval?.toUpperCase()) {
      case 'MONTHLY':
        return this.lang.instant('contracts.monthly');
      case 'QUARTERLY':
        return this.lang.instant('contracts.quarterly');
      case 'YEARLY':
        return this.lang.instant('contracts.yearly');
      default:
        return interval;
    }
  }

  analyzeContracts() {
    this.isAnalyzing = true;
    this.contractService.analyzeContracts().subscribe({
      next: (res) => {
        console.log(res);
        this.loadContracts();
        this.isAnalyzing = false;
      },
      error: (err) => {
        console.error('Analysis failed', err);
        this.isAnalyzing = false;
      }
    });
  }
}
