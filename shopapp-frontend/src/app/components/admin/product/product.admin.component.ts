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

  constructor() {
    super();
  }

  ngOnInit() {
    this.getProducts(0, 100);
  }

  getProducts(page: number, limit: number) {
    this.productService.getProducts(page, limit).subscribe({
      next: (apiresponse: ApiResponse) => {
        const productsArray: Product[] = Array.isArray(apiresponse.data) ? apiresponse.data : (apiresponse.data?.products ?? []);
        productsArray.forEach((product: Product) => {
          product.url = product?.thumbnail ? `${environment.apiBaseUrl}/products/images/${product.thumbnail}` : `${environment.apiBaseUrl}/products/images/notfound.jpeg`;
        });
        this.products = productsArray;
      },
      complete: () => { debugger },
      error: (error: HttpErrorResponse) => {
        this.toastService.showToast({
          error: error,
          defaultMsg: 'Lỗi tải danh sách sản phẩm',
          title: 'Lỗi Tải Dữ Liệu'
        });
      }
    });
  }

}