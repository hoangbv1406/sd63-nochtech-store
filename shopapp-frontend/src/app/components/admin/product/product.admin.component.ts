// File: product.admin.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BaseComponent } from '../../base/base.component';
import { Product } from '../../../models/product';
import { ApiResponse } from '../../../responses/api.response';
import { environment } from '../../../../environments/environment';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-product-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './product.admin.component.html',
  styleUrl: './product.admin.component.scss'
})
export class ProductAdminComponent extends BaseComponent implements OnInit {
  products: Product[] = [];
  keyword: string = "";
  currentPage: number = 0;
  itemsPerPage: number = 10;
  totalPages: number = 0;
  visiblePages: number[] = [];

  constructor() {
    super();
  }

  ngOnInit() {
    this.getProducts(this.keyword, this.currentPage, this.itemsPerPage);
  }

  searchProducts() {
    this.currentPage = 0;
    this.getProducts(this.keyword, this.currentPage, this.itemsPerPage);
  }

  getProducts(keyword: string, page: number, limit: number) {
    this.productService.getProducts(keyword, 0, page, limit).subscribe({
      next: (apiResponse: ApiResponse) => {
        const responseData = apiResponse.data;
        const productsArray: Product[] = responseData.products || [];
        this.totalPages = responseData.totalPages || 0;
        this.visiblePages = this.generateVisiblePageArray(this.currentPage, this.totalPages);

        productsArray.forEach((product: Product) => {
          if (product.thumbnail && (product.thumbnail.startsWith('http://') || product.thumbnail.startsWith('https://'))) {
            product.url = product.thumbnail;
          } else {
            product.url = `${environment.apiBaseUrl}/products/images/${product.thumbnail}`;
          }
        });

        this.products = productsArray;
      },
      complete: () => {
        debugger
      },
      error: (error: HttpErrorResponse) => {
        this.toastService.showToast({
          error: error,
          defaultMsg: 'Lỗi tải danh sách sản phẩm',
          title: 'Lỗi Tải Dữ Liệu'
        });
      }
    });
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.getProducts(this.keyword, this.currentPage, this.itemsPerPage);
  }

  generateVisiblePageArray(currentPage: number, totalPages: number): number[] {
    const maxVisiblePages = 5;
    const halfVisible = Math.floor(maxVisiblePages / 2);

    let startPage = Math.max(currentPage - halfVisible, 0);
    let endPage = Math.min(startPage + maxVisiblePages - 1, totalPages - 1);

    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(endPage - maxVisiblePages + 1, 0);
    }

    return new Array(endPage - startPage + 1).fill(0).map((_, index) => startPage + index);
  }

  insertProduct() {
    this.router.navigate(['/admin/products/insert']);
  }

  updateProduct(productId: number) {
    this.router.navigate(['/admin/products/update', productId]);
  }

  deleteProduct(product: Product) {
    const confirmation = window.confirm('Bạn có chắc chắn muốn xóa sản phẩm này?');
    if (confirmation) {
      this.productService.deleteProduct(product.id).subscribe({
        next: (apiResponse: ApiResponse) => {
          this.toastService.showToast({
            error: null,
            defaultMsg: 'Xóa sản phẩm thành công',
            title: 'Thành Công'
          });
          this.getProducts(this.keyword, this.currentPage, this.itemsPerPage);
        },
        complete: () => { debugger },
        error: (error: HttpErrorResponse) => {
          this.toastService.showToast({
            error: error,
            defaultMsg: 'Lỗi khi xóa sản phẩm',
            title: 'Lỗi Xóa'
          });
        }
      });
    }
  }

}