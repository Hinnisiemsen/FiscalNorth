import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ContractService } from '../core/services/contract.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';

@Component({
  selector: 'app-contract-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ...PAGE_HEADER_IMPORTS],
  templateUrl: './contract-create.component.html',
  styleUrl: './contract-create.component.css'
})
export class ContractCreateComponent {
  contractForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private contractService: ContractService,
    private router: Router
  ) {
    this.contractForm = this.fb.group({
      name: ['', Validators.required],
      amount: ['', [Validators.required, Validators.min(0.01)]],
      contractInterval: ['MONTHLY', Validators.required],
      startDate: [new Date().toISOString().split('T')[0], Validators.required],
      endDate: [new Date(new Date().setFullYear(new Date().getFullYear() + 1)).toISOString().split('T')[0], Validators.required], // Default 1 year
      autoDetected: [false]
    });
  }

  onSubmit() {
    if (this.contractForm.valid) {
      this.contractService.createContract(this.contractForm.value).subscribe({
        next: () => this.router.navigate(['/contracts']),
        error: (err) => console.error('Failed to create contract', err)
      });
    }
  }

  cancel() {
    this.router.navigate(['/contracts']);
  }
}
