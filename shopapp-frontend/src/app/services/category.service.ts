// File: category.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../responses/api.response';
import { Observable } from 'rxjs/internal/Observable';
import { InsertCategoryDTO } from '../dtos/category/insert.category.dto';
import { UpdateCategoryDTO } from '../dtos/category/update.category.dto';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private apiBaseUrl = environment.apiBaseUrl;
  constructor(private http: HttpClient) { }

  getCategories(page: number, limit: number): Observable<ApiResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());
    return this.http.get<ApiResponse>(`${environment.apiBaseUrl}/categories`, { params });
  }

  getDetailCategory(id: number): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.apiBaseUrl}/categories/${id}`);
  }

  insertCategory(insertCategoryDTO: InsertCategoryDTO): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.apiBaseUrl}/categories`, insertCategoryDTO);
  }

  updateCategory(id: number, updatedCategory: UpdateCategoryDTO): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(`${this.apiBaseUrl}/categories/${id}`, updatedCategory);
  }

}
