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
import { filter, Subscription } from 'rxjs';

@Component({
    selector: 'app-layout',
    standalone: true,
    imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
    templateUrl: './layout.component.html',
    styleUrls: ['./layout.component.css'],
})
export class LayoutComponent implements OnInit, OnDestroy {
    private readonly router = inject(Router);
    private readonly route = inject(ActivatedRoute);
    private navSub?: Subscription;

    drawerOpen = false;
    pageTitle = 'Fiscal North';

    menuItems = [
        { label: 'Dashboard', path: '/', icon: 'dashboard' },
        { label: 'Assistant', path: '/assistant', icon: 'smart_toy' },
        { label: 'Transactions', path: '/transactions', icon: 'receipt_long' },
        { label: 'Import', path: '/transactions/import', icon: 'upload' },
        { label: 'Contracts', path: '/contracts', icon: 'description' },
        { label: 'Budgets', path: '/budgets', icon: 'pie_chart' },
        { label: 'Accounts', path: '/accounts', icon: 'account_balance' },
        { label: 'Bank verbinden', path: '/bank-sync', icon: 'link' },
        { label: 'Categories', path: '/categories', icon: 'category' },
    ];

    ngOnInit(): void {
        this.updateTitle();
        this.navSub = this.router.events
            .pipe(filter((e) => e instanceof NavigationEnd))
            .subscribe(() => this.updateTitle());
    }

    ngOnDestroy(): void {
        this.navSub?.unsubscribe();
        document.body.style.overflow = '';
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
        this.pageTitle = child?.snapshot.data['title'] ?? 'Fiscal North';
    }
}
