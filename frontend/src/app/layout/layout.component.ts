import { Component, HostListener, OnDestroy, OnInit, inject } from '@angular/core';
import {
    ActivatedRoute,
    NavigationEnd,
    Router,
    RouterLink,
    RouterLinkActive,
    RouterOutlet,
} from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter, interval, Subscription, switchMap, startWith } from 'rxjs';
import { NotificationService } from '../core/services/notification.service';
import { LanguageSwitcherComponent } from '../shared/language-switcher.component';
import { UserMenuComponent } from './user-menu.component';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

@Component({
    selector: 'app-layout',
    standalone: true,
    imports: [
        CommonModule,
        RouterOutlet,
        RouterLink,
        RouterLinkActive,
        LanguageSwitcherComponent,
        UserMenuComponent,
        ...TRANSLATE_IMPORTS,
    ],
    templateUrl: './layout.component.html',
    styleUrls: ['./layout.component.css'],
})
export class LayoutComponent implements OnInit, OnDestroy {
    private readonly router = inject(Router);
    private readonly route = inject(ActivatedRoute);
    private readonly translate = inject(TranslateService);
    private navSub?: Subscription;

    drawerOpen = false;
    pageTitle = 'Fiscal North';
    unreadNotifications = 0;
    private countSub?: Subscription;

    menuItems = [
        { labelKey: 'nav.dashboard', path: '/', icon: 'dashboard' },
        { labelKey: 'nav.assistant', path: '/assistant', icon: 'auto_awesome' },
        { labelKey: 'nav.transactions', path: '/transactions', icon: 'receipt_long' },
        { labelKey: 'nav.import', path: '/transactions/import', icon: 'upload' },
        { labelKey: 'nav.contracts', path: '/contracts', icon: 'description' },
        { labelKey: 'nav.budgets', path: '/budgets', icon: 'pie_chart' },
        { labelKey: 'nav.accounts', path: '/accounts', icon: 'account_balance' },
        { labelKey: 'nav.bankSync', path: '/bank-sync', icon: 'link' },
        { labelKey: 'nav.categories', path: '/categories', icon: 'category' },
    ];

    constructor(private notificationService: NotificationService) {}

    ngOnInit(): void {
        this.updateTitle();
        this.navSub = this.router.events
            .pipe(filter((e) => e instanceof NavigationEnd))
            .subscribe(() => {
                this.updateTitle();
                this.refreshUnreadCount();
            });
        this.translate.onLangChange.subscribe(() => this.updateTitle());
        this.countSub = interval(60_000)
            .pipe(
                startWith(0),
                switchMap(() => this.notificationService.unreadCount())
            )
            .subscribe({
                next: (res) => {
                    this.unreadNotifications = res.count;
                },
            });
        this.refreshUnreadCount();
    }

    ngOnDestroy(): void {
        this.navSub?.unsubscribe();
        this.countSub?.unsubscribe();
        document.body.style.overflow = '';
    }

    private refreshUnreadCount(): void {
        this.notificationService.unreadCount().subscribe({
            next: (res) => {
                this.unreadNotifications = res.count;
            },
        });
    }

    @HostListener('document:keydown.escape')
    onEscape(): void {
        this.closeDrawer();
    }

    toggleDrawer(): void {
        this.drawerOpen = !this.drawerOpen;
        this.syncBodyScroll();
    }

    closeDrawer(): void {
        this.drawerOpen = false;
        this.syncBodyScroll();
    }

    private syncBodyScroll(): void {
        document.body.style.overflow = this.drawerOpen ? 'hidden' : '';
    }

    onNavClick(): void {
        this.closeDrawer();
    }

    private updateTitle(): void {
        let child = this.route.firstChild;
        while (child?.firstChild) {
            child = child.firstChild;
        }
        const titleKey = child?.snapshot.data['titleKey'] as string | undefined;
        this.pageTitle = titleKey
            ? this.translate.instant(titleKey)
            : this.translate.instant('layout.brand');
    }
}
