import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CategoryService, Category } from '../core/services/category.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';

@Component({
    selector: 'app-category-list',
    standalone: true,
    imports: [CommonModule, RouterLink, ...PAGE_HEADER_IMPORTS],
    templateUrl: './category-list.component.html',
    styleUrl: './category-list.component.css'
})
export class CategoryListComponent implements OnInit {
    categories: Category[] = [];

    constructor(private categoryService: CategoryService) {}

    ngOnInit() {
        this.categoryService.getCategories().subscribe((data) => (this.categories = data));
    }

    deleteCategory(id: number) {
        if (confirm('Delete this category?')) {
            this.categoryService.deleteCategory(id).subscribe({
                next: () => (this.categories = this.categories.filter((c) => c.id !== id)),
                error: (err) => console.error('Delete failed', err)
            });
        }
    }
}
