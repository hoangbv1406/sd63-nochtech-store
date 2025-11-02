// File: user.admin.component.ts
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { BaseComponent } from '../../base/base.component';
import { UserResponse } from '../../../responses/user.response';
import { ApiResponse } from '../../../responses/api.response';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-user.admin',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './user.admin.component.html',
  styleUrl: './user.admin.component.scss',
})
export class UserAdminComponent extends BaseComponent implements OnInit {
  route = inject(ActivatedRoute);

  users: UserResponse[] = [];
  currentPage: number = 0;
  itemsPerPage: number = 12;
  pages: number[] = [];
  keyword: string = "";
  localStorage?: Storage;

  constructor() {
    super()
    this.localStorage = document.defaultView?.localStorage;
  }

  ngOnInit(): void {
    this.currentPage = Number(this.localStorage?.getItem('currentUserAdminPage')) || 0;
    this.getUsers(this.keyword, this.currentPage, this.itemsPerPage);
  }

  getUsers(keyword: string, page: number, limit: number) {
    this.userService.getUsers({ keyword, page, limit }).subscribe({
      next: (apiResponse: ApiResponse) => {
        console.log('GET /users =>', apiResponse);
        const usersArray = Array.isArray(apiResponse.data) ? apiResponse.data : (apiResponse.data?.users ?? apiResponse.data?.items ?? []);
        this.users = usersArray.map((u: any) => {
          return {
            ...u,
            fullname: u.fullname ?? u.fullName ?? u.name ?? '',
            phone_number: u.phone_number ?? u.phoneNumber ?? u.phone ?? '',
            address: u.address ?? u.Address ?? u.location ?? '',
            is_active: (u.is_active ?? u.isActive ?? u.active ?? false),
            role: (typeof u.role === 'string' ? { name: u.role } : (u.role ?? { name: (u.roleName ?? u.role_name ?? '') }))
          } as UserResponse;
        });
      },
      complete: () => { debugger },
      error: (error: HttpErrorResponse) => {
        this.toastService.showToast({
          error: error,
          defaultMsg: 'Lỗi tải danh sách người dùng',
          title: 'Lỗi Tải Dữ Liệu'
        });
      }
    });
  }

  toggleUserStatus(user: UserResponse) {
    let confirmation: boolean;
    if (user.is_active) {
      confirmation = window.confirm('Are you sure you want to block this user?');
    } else {
      confirmation = window.confirm('Are you sure you want to enable this user?');
    }
    if (confirmation) {
      const params = { userId: user.id, enable: !user.is_active };
      this.userService.toggleUserStatus(params).subscribe({
        next: (response: any) => {
          console.error('Block/unblock user successfully');
          location.reload();
        },
        complete: () => { debugger },
        error: (error: HttpErrorResponse) => {
          this.toastService.showToast({
            error: error,
            defaultMsg: 'Lỗi thay đổi trạng thái người dùng',
            title: 'Lỗi Hệ Thống'
          });
        }
      });
    }
  }

}
