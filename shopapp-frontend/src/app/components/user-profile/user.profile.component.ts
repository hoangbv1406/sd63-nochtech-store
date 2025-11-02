// File: user.profile.component.ts
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { BaseComponent } from '../base/base.component';
import { UserResponse } from '../../responses/user.response';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { UpdateUserDTO } from '../../dtos/user/update.user.dto';

@Component({
  selector: 'user-profile',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent, FormsModule, ReactiveFormsModule],
  templateUrl: './user.profile.component.html',
  styleUrl: './user.profile.component.scss',
})
export class UserProfileComponent extends BaseComponent implements OnInit {
  formBuilder: FormBuilder = inject(FormBuilder);
  userResponse?: UserResponse;
  token: string = '';

  userProfileForm: FormGroup = this.formBuilder.group({
    fullname: [''],
    address: [''],
    password: [''],
    retype_password: [''],
    date_of_birth: [Date.now()],
  });

  ngOnInit(): void {
    this.token = this.tokenService.getToken();
    this.userService.getUserDetail(this.token).subscribe({
      next: (response: any) => {
        this.userResponse = { ...response.data, date_of_birth: new Date(response.data.date_of_birth) };
        this.userProfileForm.patchValue({
          fullname: this.userResponse?.fullname || '',
          address: this.userResponse?.address || '',
          date_of_birth: this.userResponse?.date_of_birth.toISOString().substring(0, 10),
        });
        this.userService.saveUserResponseToLocalStorage(this.userResponse);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Lỗi khi lấy thông tin người dùng:', error?.error?.message ?? '');
      },
    });
  }

  save(): void {
    if (this.userProfileForm.valid) {
      const updateUserDTO: UpdateUserDTO = {
        fullname: this.userProfileForm.get('fullname')?.value,
        address: this.userProfileForm.get('address')?.value,
        password: this.userProfileForm.get('password')?.value,
        retype_password: this.userProfileForm.get('retype_password')?.value,
        date_of_birth: this.userProfileForm.get('date_of_birth')?.value
      };
      this.userService.updateUserDetail(this.token, updateUserDTO)
        .subscribe({
          next: (response: any) => {
            this.userService.removeUserFromLocalStorage();
            this.tokenService.removeToken();
            this.router.navigate(['/login']);
          },
          error: (error: HttpErrorResponse) => {
            debugger;
            console.error(error?.error?.message ?? '');
          }
        });
    } else {
      if (this.userProfileForm.hasError('passwordMismatch')) {
        console.error('Mật khẩu và mật khẩu gõ lại chưa chính xác')
      }
    }
  }
}
