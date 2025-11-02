// File: user.service.ts
import { Inject, inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { HttpUtilService } from './http.util.service';
import { DOCUMENT } from '@angular/common';
import { UserResponse } from '../responses/user.response';
import { LoginDTO } from '../dtos/user/login.dto';
import { ApiResponse } from '../responses/api.response';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { UpdateUserDTO } from '../dtos/user/update.user.dto';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private httpUtilService = inject(HttpUtilService);
  private apiConfig = { headers: this.httpUtilService.createHeaders() };
  private apiLogin = `${environment.apiBaseUrl}/users/login`;
  private apiUserDetail = `${environment.apiBaseUrl}/users/details`;
  localStorage?: Storage;

  constructor(@Inject(DOCUMENT) private document: Document) {
    this.localStorage = document.defaultView?.localStorage;
  }

  saveUserResponseToLocalStorage(userResponse?: UserResponse) {
    try {
      if (userResponse == null || !userResponse) {
        return;
      }
      const userResponseJSON = JSON.stringify(userResponse);
      this.localStorage?.setItem('user', userResponseJSON);
      console.log('User response saved to local storage.');
    } catch (error) {
      console.error('Error saving user response to local storage:', error);
    }
  }

  getUserResponseFromLocalStorage(): UserResponse | null {
    try {
      const userResponseJSON = this.localStorage?.getItem('user');
      if (userResponseJSON == null || userResponseJSON == undefined) {
        return null;
      }
      const userResponse = JSON.parse(userResponseJSON!);
      console.log('User response retrieved from local storage.');
      return userResponse;
    } catch (error) {
      console.error('Error retrieving user response from local storage:', error);
      return null;
    }
  }

  removeUserFromLocalStorage(): void {
    try {
      this.localStorage?.removeItem('user');
      console.log('User data removed from local storage.');
    } catch (error) {
      console.error('Error removing user data from local storage:', error);
    }
  }

  login(loginDTO: LoginDTO): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(this.apiLogin, loginDTO, this.apiConfig);
  }

  getUserDetail(token: string): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(this.apiUserDetail, null, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json', Authorization: `Bearer ${token}` })
    });
  }

  updateUserDetail(token: string, updateUserDTO: UpdateUserDTO): Observable<ApiResponse> {
    let userResponse = this.getUserResponseFromLocalStorage();
    return this.http.put<ApiResponse>(`${this.apiUserDetail}/${userResponse?.id}`, updateUserDTO, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json', Authorization: `Bearer ${token}` })
    })
  }

  getUsers(params: { page: number, limit: number, keyword: string }): Observable<ApiResponse> {
    const url = `${environment.apiBaseUrl}/users`;
    return this.http.get<ApiResponse>(url, { params: params });
  }

  toggleUserStatus(params: { userId: number, enable: boolean }): Observable<ApiResponse> {
    const url = `${environment.apiBaseUrl}/users/block/${params.userId}/${params.enable ? '1' : '0'}`;
    return this.http.put<ApiResponse>(url, null, this.apiConfig);
  }

}
