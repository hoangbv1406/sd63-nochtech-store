// // File: product-list.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BaseComponent } from '../base/base.component';
import { Product } from '../../models/product';
import { Category } from '../../models/category';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';

@Component({
    selector: 'app-product-list',
    standalone: true,
    imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent],
    templateUrl: './product.list.component.html',
    styleUrl: './product.list.component.scss'
})
export class ProductListComponent extends BaseComponent implements OnInit {
    products: Product[] = [];
    categories: Category[] = [];
    categoryId: number = 0;
    keyword: string = '';
    currentPage: number = 0;
    itemsPerPage: number = 12;
    totalPages: number = 0;
    visiblePages: number[] = [];

    ngOnInit(): void {
        this.getCategories();
        this.activatedRoute.queryParams.subscribe(params => {
            this.categoryId = Number(params['category_id']) || 0;
            this.keyword = params['keyword'] || '';
            this.currentPage = Number(params['page']) || 0;
            this.getProducts();
        });
    }

    getCategories() {
        this.categoryService.getCategories(0, 100).subscribe({
            next: (response: any) => {
                this.categories = response.data;
            },
            error: (err) => console.error('Lỗi lấy danh mục:', err)
        });
    }

    getProducts() {
        this.productService.getProducts(
            this.keyword,
            this.categoryId,
            this.currentPage,
            this.itemsPerPage
        ).subscribe({
            next: (response: any) => {
                const productsRaw = response.data?.products || [];
                this.totalPages = response.data?.totalPages || 0;

                this.products = productsRaw.map((product: Product) => {
                    if (product.thumbnail) {
                        product.url = product.thumbnail;
                    }
                    return product;
                });

                this.generateVisiblePages();
            },
            error: (err) => console.error('Lỗi lấy sản phẩm:', err)
        });
    }

    onPageChange(page: number) {
        this.currentPage = page;
        this.router.navigate([], {
            relativeTo: this.activatedRoute,
            queryParams: { page: this.currentPage },
            queryParamsHandling: 'merge'
        });
    }

    searchByCategory(categoryId: number) {
        this.categoryId = categoryId;
        this.currentPage = 0;

        this.router.navigate([], {
            relativeTo: this.activatedRoute,
            queryParams: {
                category_id: categoryId,
                page: 0
            },
            queryParamsHandling: 'merge'
        });
    }

    onProductClick(productId: number) {
        this.router.navigate(['/products', productId]);
    }

    generateVisiblePages() {
        this.visiblePages = [];
        const startPage = Math.max(0, this.currentPage - 2);
        const endPage = Math.min(this.totalPages - 1, this.currentPage + 2);

        for (let i = startPage; i <= endPage; i++) {
            this.visiblePages.push(i);
        }
    }

}