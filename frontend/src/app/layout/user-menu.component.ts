import { Component, HostListener, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { UserProfile } from '../core/services/user.service';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

@Component({
  selector: 'app-user-menu',
  standalone: true,
  imports: [CommonModule, RouterLink, ...TRANSLATE_IMPORTS],
  templateUrl: './user-menu.component.html',
  styleUrl: './user-menu.component.css',
})
export class UserMenuComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  open = false;
  user: UserProfile | null = null;

  constructor() {
    this.auth.currentUser$.subscribe((user) => {
      this.user = user;
    });
    this.auth.refreshSession().subscribe();
  }

  toggle(): void {
    this.open = !this.open;
  }

  close(): void {
    this.open = false;
  }

  signOut(): void {
    this.close();
    this.auth.logout().subscribe();
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.close();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.user-menu-root')) {
      this.close();
    }
  }
}
