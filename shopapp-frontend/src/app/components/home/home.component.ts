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

  promoProducts: Product[] = [];
  promoIndex: number = 0;
  itemsPerPage: number = 4;

  currentDoubleSlide = 0;
  totalDoubleSlides = 2;

  suggestionIndex: number = 0;
  suggestionLimit: number = 12;

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(params => {
      this.selectedCategoryId = Number(params['categoryId']) || 0;
    });
    this.getCategories(0, 100);
    this.getProducts(0, 100);
    this.getPromoProducts();
  }

  getCategories(page: number, limit: number) {
    this.categoryService.getCategories(page, limit).subscribe({
      next: (apiResponse: ApiResponse) => {
        this.categories = apiResponse.data;
      }
    });
  }

  getProducts(page: number, limit: number) {
    this.productService.getProducts('', 0, page, limit).subscribe({
      next: (apiresponse: ApiResponse) => {
        const productsArray: Product[] = Array.isArray(apiresponse.data) ? apiresponse.data : (apiresponse.data?.products ?? []);
        productsArray.forEach((product: Product) => {
          if (product.thumbnail && (product.thumbnail.startsWith('http'))) {
            product.url = product.thumbnail;
          } else {
            product.url = `${environment.apiBaseUrl}/products/images/${product.thumbnail}`;
          }
        });
        this.products = productsArray;
      }
    });
  }

  get visibleSuggestionProducts(): Product[] {
    return this.products.slice(this.suggestionIndex, this.suggestionIndex + this.suggestionLimit);
  }

  nextSuggestion() {
    if (this.suggestionIndex + this.suggestionLimit < this.products.length) {
      this.suggestionIndex += 6;
    }
  }

  prevSuggestion() {
    if (this.suggestionIndex > 0) {
      this.suggestionIndex -= 6;
    }
  }

  getPromoProducts() {
    this.productService.getProducts('', 0, 0, 12, 'price', 'desc').subscribe({
      next: (response: ApiResponse) => {
        const raw = response.data?.products ?? [];
        this.promoProducts = raw.map((p: Product) => {
          p.url = (p.thumbnail && p.thumbnail.startsWith('http')) ? p.thumbnail : `${environment.apiBaseUrl}/products/images/${p.thumbnail}`;
          return p;
        });
      }
    });
  }

  get visiblePromoProducts(): Product[] {
    return this.promoProducts.slice(this.promoIndex, this.promoIndex + this.itemsPerPage);
  }

  nextDouble() {
    this.currentDoubleSlide = (this.currentDoubleSlide < this.totalDoubleSlides - 1) ? this.currentDoubleSlide + 1 : 0;
  }

  prevDouble() {
    this.currentDoubleSlide = (this.currentDoubleSlide > 0) ? this.currentDoubleSlide - 1 : this.totalDoubleSlides - 1;
  }

  nextPromo() {
    if (this.promoIndex + this.itemsPerPage < this.promoProducts.length) this.promoIndex++;
  }

  prevPromo() {
    if (this.promoIndex > 0) this.promoIndex--;
  }

  onProductClick(productId: number) {
    this.router.navigate(['/products', productId]);
  }

}
