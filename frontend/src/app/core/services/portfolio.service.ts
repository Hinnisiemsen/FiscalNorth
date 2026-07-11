import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export type AssetClass = 'STOCK' | 'ETF' | 'FUND' | 'OTHER';

export interface HoldingView {
  id: number;
  symbol: string;
  name: string;
  quantity: number;
  costBasis: number;
  assetClass: AssetClass;
  currentPrice: number;
  marketValue: number;
  unrealizedGain: number;
  lastUpdatedBy: string | null;
  priceStale: boolean;
}

export interface PortfolioOverview {
  id: number;
  name: string;
  totalValue: number;
  totalCost: number;
  unrealizedGain: number;
  holdings: HoldingView[];
}

export interface CreateHoldingRequest {
  symbol: string;
  name: string;
  quantity: number;
  costBasis: number;
  assetClass: AssetClass;
}

@Injectable({ providedIn: 'root' })
export class PortfolioService {
  constructor(private api: ApiService) {}

  getPortfolio(): Observable<PortfolioOverview> {
    return this.api.get<PortfolioOverview>('/portfolio');
  }

  addHolding(request: CreateHoldingRequest): Observable<PortfolioOverview> {
    return this.api.post<PortfolioOverview>('/portfolio/holdings', request);
  }
}
