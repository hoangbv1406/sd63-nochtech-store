// File: category.admin.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BaseComponent } from '../../base/base.component';
import { Category } from '../../../models/category';
import { ApiResponse } from '../../../responses/api.response';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-category-admin',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './category.admin.component.html',
  styleUrl: './category.admin.component.scss'
})
export class CategoryAdminComponent extends BaseComponent implements OnInit {
  categories: Category[] = [];

  ngOnInit() {
    this.getCategories(0, 100);
  }

  getCategories(page: number, limit: number) {
    this.categoryService.getCategories(page, limit).subscribe({
      next: (apiResponse: ApiResponse) => { this.categories = apiResponse.data },
      complete: () => { debugger },
      error: (error: HttpErrorResponse) => {
        this.toastService.showToast({
          error: error,
          defaultMsg: 'Lỗi tải danh sách danh mục',
          title: 'Lỗi Danh Mục'
        });
      }
    });
  }

  insertCategory() {
    this.router.navigate(['/admin/categories/insert']);
  }

  updateCategory(categoryId: number) {
    this.router.navigate(['/admin/categories/update', categoryId]);
  }

  deleteCategory(category: Category) {
    const confirmation = window.confirm('Are you sure you want to delete this category?');
    if (confirmation) {
      this.categoryService.deleteCategory(category.id).subscribe({
        next: (apiResponse: ApiResponse) => {
          this.toastService.showToast({
            error: null,
            defaultMsg: 'Xóa danh mục thành công',
            title: 'Thành Công'
          });
          location.reload();
        },
        error: (error: HttpErrorResponse) => {
          this.toastService.showToast({
            error: error,
            defaultMsg: 'Lỗi khi xóa danh mục',
            title: 'Lỗi Xóa'
          });
        }
      });
    }
  }

}
