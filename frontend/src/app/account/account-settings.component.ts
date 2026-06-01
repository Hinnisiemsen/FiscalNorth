import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../core/services/auth.service';
import { UserProfile, UserService } from '../core/services/user.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';
import { LanguageService } from '../core/i18n/language.service';
import { AppLocale } from '../core/i18n/supported-locales';

@Component({
  selector: 'app-account-settings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
  templateUrl: './account-settings.component.html',
  styleUrl: './account-settings.component.css',
})
export class AccountSettingsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);
  private readonly languageService = inject(LanguageService);

  user: UserProfile | null = null;
  profileMessage = '';
  passwordMessage = '';
  profileError = '';
  passwordError = '';

  profileForm = this.fb.group({
    userName: ['', [Validators.required, Validators.minLength(2)]],
    locale: ['en', Validators.required],
  });

  passwordForm = this.fb.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
  });

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.user = user;
        this.profileForm.patchValue({
          userName: user.userName,
          locale: user.locale ?? 'en',
        });
      },
    });
  }

  get canChangePassword(): boolean {
    return this.user?.authProvider === 'LOCAL' || this.user?.authProvider === 'BOTH';
  }

  saveProfile(): void {
    if (this.profileForm.invalid) return;
    this.profileMessage = '';
    this.profileError = '';
    const value = this.profileForm.getRawValue();
    this.userService.updateProfile({
      userName: value.userName!,
      locale: value.locale!,
    }).subscribe({
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
    this.userService.changePassword({
      currentPassword: value.currentPassword!,
      newPassword: value.newPassword!,
    }).subscribe({
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
