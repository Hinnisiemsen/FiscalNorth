import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Household, HouseholdService } from '../core/services/household.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

@Component({
  selector: 'app-household-join',
  standalone: true,
  imports: [CommonModule, RouterLink, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
  templateUrl: './household-join.component.html',
  styleUrl: './household-join.component.css',
})
export class HouseholdJoinComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly householdService = inject(HouseholdService);

  token = '';
  loading = false;
  errorKey = '';
  household: Household | null = null;

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token') ?? '';
    if (!this.token) {
      this.errorKey = 'household.join.missingToken';
    }
  }

  acceptInvite(): void {
    if (!this.token) return;
    this.loading = true;
    this.errorKey = '';
    this.householdService.acceptInvite(this.token).subscribe({
      next: (household) => {
        this.household = household;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.errorKey = err.error?.messageKey ?? 'household.join.failed';
      },
    });
  }

  goToHousehold(): void {
    void this.router.navigate(['/household']);
  }
}
