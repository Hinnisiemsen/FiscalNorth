import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Household, HouseholdService } from '../core/services/household.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

@Component({
  selector: 'app-household-settings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
  templateUrl: './household-settings.component.html',
  styleUrl: './household-settings.component.css',
})
export class HouseholdSettingsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly householdService = inject(HouseholdService);

  household: Household | null = null;
  loading = true;
  inviteMessage = '';
  inviteError = '';

  inviteForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });

  ngOnInit(): void {
    this.loadHousehold();
  }

  loadHousehold(): void {
    this.loading = true;
    this.householdService.getMyHousehold().subscribe({
      next: (household) => {
        this.household = household;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  get isOwner(): boolean {
    return this.household?.members.some((m) => m.role === 'OWNER') ?? false;
  }

  get canInvite(): boolean {
    return (
      this.isOwner &&
      (this.household?.members.length ?? 0) < 2 &&
      !this.household?.pendingInvite
    );
  }

  sendInvite(): void {
    if (this.inviteForm.invalid) return;
    this.inviteMessage = '';
    this.inviteError = '';
    const email = this.inviteForm.getRawValue().email!;
    this.householdService.invitePartner(email).subscribe({
      next: () => {
        this.inviteMessage = 'household.inviteSent';
        this.inviteForm.reset();
        this.loadHousehold();
      },
      error: (err) => {
        this.inviteError = err.error?.messageKey ?? 'household.inviteFailed';
      },
    });
  }
}
