import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PortfolioOverview, PortfolioService } from '../core/services/portfolio.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

@Component({
  selector: 'app-portfolio',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
  templateUrl: './portfolio.component.html',
  styleUrl: './portfolio.component.css',
})
export class PortfolioComponent implements OnInit {
  private readonly portfolioService = inject(PortfolioService);
  private readonly fb = inject(FormBuilder);

  portfolio: PortfolioOverview | null = null;
  loading = true;
  showForm = false;
  addError = '';

  holdingForm = this.fb.group({
    symbol: ['', Validators.required],
    name: ['', Validators.required],
    quantity: [0, [Validators.required, Validators.min(0.0001)]],
    costBasis: [0, [Validators.required, Validators.min(0)]],
    assetClass: ['STOCK', Validators.required],
  });

  ngOnInit(): void {
    this.loadPortfolio();
  }

  loadPortfolio(): void {
    this.loading = true;
    this.portfolioService.getPortfolio().subscribe({
      next: (portfolio) => {
        this.portfolio = portfolio;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
    this.addError = '';
  }

  addHolding(): void {
    if (this.holdingForm.invalid) return;
    const value = this.holdingForm.getRawValue();
    this.portfolioService
      .addHolding({
        symbol: value.symbol!,
        name: value.name!,
        quantity: Number(value.quantity),
        costBasis: Number(value.costBasis),
        assetClass: value.assetClass as 'STOCK' | 'ETF' | 'FUND' | 'OTHER',
      })
      .subscribe({
        next: (portfolio) => {
          this.portfolio = portfolio;
          this.showForm = false;
          this.holdingForm.reset({ assetClass: 'STOCK', quantity: 0, costBasis: 0 });
        },
        error: (err) => {
          this.addError = err.error?.messageKey ?? 'portfolio.addFailed';
        },
      });
  }

  gainClass(gain: number): string {
    if (gain > 0) return 'gain-positive';
    if (gain < 0) return 'gain-negative';
    return '';
  }

  allocationSlices(): { label: string; value: number; share: number }[] {
    if (!this.portfolio?.holdings.length) return [];
    const total = this.portfolio.totalValue || 0;
    if (total <= 0) return [];
    return this.portfolio.holdings.map((h) => ({
      label: h.symbol,
      value: h.marketValue,
      share: (h.marketValue / total) * 100,
    }));
  }
}
