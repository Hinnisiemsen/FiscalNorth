import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CategoryService } from '../core/services/category.service';
import { PAGE_HEADER_IMPORTS } from '../shared/shared-ui';
import { TRANSLATE_IMPORTS } from '../core/i18n/translate-imports';

@Component({
    selector: 'app-category-create',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, ...PAGE_HEADER_IMPORTS, ...TRANSLATE_IMPORTS],
    templateUrl: './category-create.component.html',
    styleUrl: './category-create.component.css'
})
export class CategoryCreateComponent {
    categoryForm: FormGroup;

    constructor(
        private fb: FormBuilder,
        private categoryService: CategoryService,
        private router: Router
    ) {
        this.categoryForm = this.fb.group({
            name: ['', Validators.required],
            transactionType: ['Expense', Validators.required]
        });
    }

    onSubmit() {
        if (this.categoryForm.valid) {
            const val = this.categoryForm.value;
            this.categoryService.createCategory({ name: val.name, transactionType: val.transactionType }).subscribe({
                next: () => this.router.navigate(['/categories']),
                error: (err) => console.error('Create failed', err)
            });
        }
    }

    cancel() {
        this.router.navigate(['/categories']);
    }
}
