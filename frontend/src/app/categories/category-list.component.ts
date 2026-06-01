import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CategoryService, Category } from '../core/services/category.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';
import { LanguageService } from '../core/i18n/language.service';

@Component({
    selector: 'app-category-list',
    standalone: true,
    imports: [CommonModule, RouterLink, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
    templateUrl: './category-list.component.html',
    styleUrl: './category-list.component.css'
})
export class CategoryListComponent implements OnInit {
    private readonly lang = inject(LanguageService);

    categories: Category[] = [];

    constructor(private categoryService: CategoryService) {}

    ngOnInit() {
        this.categoryService.getCategories().subscribe((data) => (this.categories = data));
    }

    typeLabel(type: string): string {
        return type === 'Income'
            ? this.lang.instant('categories.income')
            : this.lang.instant('categories.expense');
    }

    deleteCategory(id: number) {
        if (confirm(this.lang.instant('categories.deleteConfirm'))) {
            this.categoryService.deleteCategory(id).subscribe({
                next: () => (this.categories = this.categories.filter((c) => c.id !== id)),
                error: (err) => console.error('Delete failed', err)
            });
        }
    }
}
