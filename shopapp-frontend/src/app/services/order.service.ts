// File: order.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { OrderDTO } from '../dtos/order/order.dto';
import { Observable } from 'rxjs';
import { ApiResponse } from '../responses/api.response';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private apiBaseUrl = environment.apiBaseUrl;
  private apiUrl = `${environment.apiBaseUrl}/orders`;
  private apiGetAllOrders = `${environment.apiBaseUrl}/orders/get-orders-by-keyword`;

  constructor(private http: HttpClient) { }

  placeOrder(orderData: OrderDTO): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(this.apiUrl, orderData);
  }

  getAllOrders(keyword: string, page: number, limit: number): Observable<ApiResponse> {
    const params = new HttpParams()
      .set('keyword', keyword)
      .set('page', page.toString())
      .set('limit', limit.toString());
    return this.http.get<ApiResponse>(this.apiGetAllOrders, { params });
  }

  getOrderById(orderId: number): Observable<ApiResponse> {
    const url = `${environment.apiBaseUrl}/orders/${orderId}`;
    return this.http.get<ApiResponse>(url);
  }

  updateOrder(orderId: number, orderData: OrderDTO): Observable<ApiResponse> {
    const url = `${environment.apiBaseUrl}/orders/${orderId}`;
    return this.http.put<ApiResponse>(url, orderData);
  }

  deleteOrder(orderId: number): Observable<ApiResponse> {
    const url = `${environment.apiBaseUrl}/orders/${orderId}`;
    return this.http.delete<ApiResponse>(url);
  }

  updateOrderStatus(orderId: number, status: string): Observable<ApiResponse> {
    const url = `${environment.apiBaseUrl}/orders/${orderId}/status`;
    const params = new HttpParams().set('status', status);
    return this.http.put<ApiResponse>(url, null, { params });
  }

  getOrdersByUserId(userId: number): Observable<any> {
    return this.http.get(`${environment.apiBaseUrl}/orders/user/${userId}`);
  }

}
