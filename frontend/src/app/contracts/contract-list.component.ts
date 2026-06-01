import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ContractService, Contract } from '../core/services/contract.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';

@Component({
  selector: 'app-contract-list',
  standalone: true,
  imports: [CommonModule, RouterLink, ...PAGE_HEADER_IMPORTS],
  templateUrl: './contract-list.component.html',
  styleUrl: './contract-list.component.css'
})
export class ContractListComponent implements OnInit {
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

  analyzeContracts() {
    this.isAnalyzing = true;
    this.contractService.analyzeContracts().subscribe({
      next: (res) => {
        console.log(res);
        this.loadContracts(); // Reload to see new contracts
        this.isAnalyzing = false;
      },
      error: (err) => {
        console.error('Analysis failed', err);
        this.isAnalyzing = false;
      }
    });
  }
}
