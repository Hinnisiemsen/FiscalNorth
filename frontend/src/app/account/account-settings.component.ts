import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { BillingService } from '../core/services/billing.service';
import { EntitlementService } from '../core/services/entitlement.service';
import { UserProfile, UserService } from '../core/services/user.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';
import { LanguageService } from '../core/i18n/language.service';
import { AppLocale } from '../core/i18n/supported-locales';

@Component({
  selector: 'app-account-settings',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    ...PAGE_HEADER_IMPORTS,
    ...TRANSLATE_IMPORTS,
  ],
  templateUrl: './account-settings.component.html',
  styleUrl: './account-settings.component.css',
})
export class AccountSettingsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);
  private readonly languageService = inject(LanguageService);
  private readonly billingService = inject(BillingService);
  private readonly entitlementService = inject(EntitlementService);
  private readonly route = inject(ActivatedRoute);

  user: UserProfile | null = null;
  profileMessage = '';
  passwordMessage = '';
  profileError = '';
  passwordError = '';
  billingLoading = false;
  billingError = '';
  checkoutSuccess = false;
  billingEnabled = false;

  profileForm = this.fb.group({
    userName: ['', [Validators.required, Validators.minLength(2)]],
    locale: ['en', Validators.required],
  });

  passwordForm = this.fb.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
  });

  ngOnInit(): void {
    this.checkoutSuccess = this.route.snapshot.queryParamMap.get('checkout') === 'success';
    if (this.checkoutSuccess) {
      this.entitlementService.refresh().subscribe();
    }

    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.user = user;
        this.profileForm.patchValue({
          userName: user.userName,
          locale: user.locale ?? 'en',
        });
      },
    });

    this.billingService.getSubscription().subscribe({
      next: (sub) => {
        this.billingEnabled = sub.billingEnabled;
      },
    });
  }

  get canChangePassword(): boolean {
    return this.user?.authProvider === 'LOCAL' || this.user?.authProvider === 'BOTH';
  }

  get subscription() {
    return this.entitlementService.subscription;
  }

  get isPremium(): boolean {
    return this.entitlementService.isPremium;
  }

  openPortal(): void {
    this.billingLoading = true;
    this.billingError = '';
    this.billingService.createPortalSession().subscribe({
      next: (session) => {
        window.location.href = session.url;
      },
      error: () => {
        this.billingLoading = false;
        this.billingError = 'billing.portalFailed';
      },
    });
  }

  saveProfile(): void {
    if (this.profileForm.invalid) return;
    this.profileMessage = '';
    this.profileError = '';
    const value = this.profileForm.getRawValue();
    this.userService
      .updateProfile({
        userName: value.userName!,
        locale: value.locale!,
      })
      .subscribe({
        next: (updated) => {
          this.user = updated;
          this.authService.loadCurrentUser().subscribe();
          this.languageService.setLanguage(updated.locale as AppLocale);
          this.profileMessage = 'Profile updated';
        },
        error: (err) => {
          this.profileError = err.error?.message ?? 'Could not update profile';
        },
      });
  }

  changePassword(): void {
    if (this.passwordForm.invalid) return;
    this.passwordMessage = '';
    this.passwordError = '';
    const value = this.passwordForm.getRawValue();
    this.userService
      .changePassword({
        currentPassword: value.currentPassword!,
        newPassword: value.newPassword!,
      })
      .subscribe({
        next: () => {
          this.passwordMessage = 'Password updated';
          this.passwordForm.reset();
        },
        error: (err) => {
          this.passwordError = err.error?.message ?? 'Could not change password';
        },
      });
  }
}
