// File: product.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../responses/api.response';
import { Observable } from 'rxjs';
import { InsertProductDTO } from '../dtos/product/insert.product.dto';
import { UpdateProductDTO } from '../dtos/product/update.product.dto';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiBaseUrl = environment.apiBaseUrl;
  constructor(private http: HttpClient) { }

  getProducts(page: number, limit: number): Observable<ApiResponse> {
    const params = {
      page: page.toString(),
      limit: limit.toString()
    };
    return this.http.get<ApiResponse>(`${this.apiBaseUrl}/products`, { params });
  }

  getDetailProduct(productId: number): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.apiBaseUrl}/products/${productId}`);
  }

  insertProduct(insertProductDTO: InsertProductDTO): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.apiBaseUrl}/products`, insertProductDTO);
  }

  updateProduct(productId: number, updatedProduct: UpdateProductDTO): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(`${this.apiBaseUrl}/products/${productId}`, updatedProduct);
  }

  uploadImages(productId: number, files: File[]): Observable<ApiResponse> {
    const formData = new FormData();
    for (let i = 0; i < files.length; i++) { formData.append('files', files[i]) }
    return this.http.post<ApiResponse>(`${this.apiBaseUrl}/products/uploads/${productId}`, formData);
  }

  deleteProductImage(id: number): Observable<any> {
    return this.http.delete<string>(`${this.apiBaseUrl}/product-images/${id}`);
  }

}
