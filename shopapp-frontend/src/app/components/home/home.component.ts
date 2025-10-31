// File: home.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { Category } from '../../models/category';
import { FormsModule } from '@angular/forms';
import { BaseComponent } from '../base/base.component';
import { ApiResponse } from '../../responses/api.response';
import { Product } from '../../models/product';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, FooterComponent, HeaderComponent, FormsModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent extends BaseComponent implements OnInit {
  categories: Category[] = [];
  selectedCategoryId: number = 0;
  products: Product[] = [];

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(params => {
      this.selectedCategoryId = Number(params['categoryId']) || 0;
    });
    this.getCategories(0, 100);
    this.getProducts(0, 100);
  }

  getCategories(page: number, limit: number) {
    this.categoryService.getCategories(page, limit).subscribe({
      next: (apiResponse: ApiResponse) => {
        debugger;
        this.categories = apiResponse.data;
      },
      complete: () => {
        debugger;
      },
    });
  }

  getProducts(page: number, limit: number) {
    this.productService.getProducts(page, limit).subscribe({
      next: (apiresponse: ApiResponse) => {
        const productsArray: Product[] = Array.isArray(apiresponse.data) ? apiresponse.data : (apiresponse.data?.products ?? []);
        productsArray.forEach((product: Product) => {
          product.url = `${environment.apiBaseUrl}/products/images/${product.thumbnail}`;
        });
        this.products = productsArray;
      },
      complete: () => {
        debugger;
      },
    });
  }

  onProductClick(productId: number) {
    this.router.navigate(['/products', productId]);
  }

}
