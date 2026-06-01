import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Category {
  id: number;
  name: string;
  transactionType: string;
}

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  constructor(private apiService: ApiService) {}

  getCategories(): Observable<Category[]> {
    return this.apiService.get<Category[]>('/category');
  }

  createCategory(category: { name: string; transactionType: string }): Observable<Category> {
    return this.apiService.post<Category>('/category', category);
  }

  deleteCategory(id: number): Observable<void> {
    return this.apiService.delete<void>(`/category/${id}`);
  }
}
