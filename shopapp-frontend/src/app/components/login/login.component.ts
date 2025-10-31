// File: login.component.ts
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { FormsModule, NgForm } from '@angular/forms';
import { BaseComponent } from '../base/base.component';
import { UserResponse } from '../../responses/user.response';
import { LoginDTO } from '../../dtos/user/login.dto';
import { ApiResponse } from '../../responses/api.response';
import { catchError, finalize, of, switchMap, tap } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent extends BaseComponent implements OnInit {
  @ViewChild('loginForm') loginForm!: NgForm;

  userResponse?: UserResponse;
  phoneNumber: string = '';
  password: string = '';
  rememberMe: boolean = true;

  ngOnInit() {
    debugger
  }

  login() {
    const loginDTO: LoginDTO = { phone_number: this.phoneNumber, password: this.password };
    this.userService.login(loginDTO).pipe(tap((apiResponse: ApiResponse) => {
      const { token } = apiResponse.data; this.tokenService.setToken(token);
    }),
      switchMap((apiResponse: ApiResponse) => {
        const { token } = apiResponse.data;
        return this.userService.getUserDetail(token).pipe(tap((apiResponse2: ApiResponse) => {
          this.userResponse = { ...apiResponse2.data, date_of_birth: new Date(apiResponse2.data.date_of_birth) };
          if (this.rememberMe) {
            this.userService.saveUserResponseToLocalStorage(this.userResponse);
          }
          if (this.userResponse?.role.name === 'admin') {
            this.router.navigate(['/admin']);
          }
          if (this.userResponse?.role.name === 'user') {
            this.router.navigate(['/']);
          }
        }),
          catchError((error: HttpErrorResponse) => {
            console.error('Lỗi khi lấy thông tin người dùng:', error?.error?.message ?? '');
            return of(null);
          })
        );
      }),
      finalize(() => { this.cartService.refreshCart() })
    ).subscribe({
      error: (error: HttpErrorResponse) => {
        this.toastService.showToast({
          error: error,
          defaultMsg: 'Sai thông tin đăng nhập',
          title: 'Lỗi Đăng Nhập'
        });
      }
    });
  }
}
