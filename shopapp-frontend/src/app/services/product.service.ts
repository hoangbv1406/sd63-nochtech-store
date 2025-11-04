// File: product.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../responses/api.response';
import { Observable } from 'rxjs';
import { InsertProductDTO } from '../dtos/product/insert.product.dto';
import { UpdateProductDTO } from '../dtos/product/update.product.dto';
import { forkJoin, of, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiBaseUrl = environment.apiBaseUrl;
  constructor(private http: HttpClient) { }

  getProducts(
    keyword: string,
    categoryId: number,
    page: number,
    limit: number,
    sortBy: string = 'id',
    sortDir: string = 'asc'
  ): Observable<ApiResponse> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString())
      .set('sort_by', sortBy)
      .set('sort_dir', sortDir);

    if (keyword && keyword.trim() !== '') {
      params = params.set('keyword', keyword);
    }

    if (categoryId > 0) {
      params = params.set('category_id', categoryId.toString());
    }

    return this.http.get<ApiResponse>(`${this.apiBaseUrl}/products`, { params });
  }

  getProductsByIds(productIds: number[]): Observable<ApiResponse> {
    const idsStr = productIds.join(',');
    const params = new HttpParams().set('productsByIds', idsStr);

    return this.http.get<ApiResponse>(`${this.apiBaseUrl}/products/by-ids`, { params }).pipe(
      catchError(err => {
        if (err.status === 500 || err.status === 0) {
          const requests = productIds.map(id =>
            this.getDetailProduct(id).pipe(
              map((res: ApiResponse) => res.data),
              catchError(e => {
                console.warn(`[ProductService] getDetailProduct(${id}) failed`, e);
                return of(null);
              })
            )
          );
          return forkJoin(requests).pipe(
            map((products: any[]) => {
              return {
                status: '200',
                message: 'Fallback: fetched products individually',
                data: products.filter(p => p != null)
              } as ApiResponse;
            })
          );
        }
        return throwError(() => err);
      })
    );
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

  deleteProduct(productId: number): Observable<ApiResponse> {
    return this.http.delete<ApiResponse>(`${this.apiBaseUrl}/products/${productId}`);
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
