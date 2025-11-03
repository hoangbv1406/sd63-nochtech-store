// File: product.detail.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { BaseComponent } from '../base/base.component';
import { ApiResponse } from '../../responses/api.response';
import { environment } from '../../../environments/environment';
import { HttpErrorResponse } from '@angular/common/http';
import { Product } from '../../models/product';

@Component({
  selector: 'app-detail-product',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent],
  templateUrl: './product.detail.component.html',
  styleUrl: './product.detail.component.scss'
})
export class DetailProductComponent extends BaseComponent implements OnInit {
  product?: Product;
  productId: number = 0;
  currentImageIndex: number = 0;
  quantity: number = 1;
  isPressedAddToCart: boolean = false;

  ngOnInit() {
    const idParam = this.activatedRoute.snapshot.paramMap.get('id');
    if (idParam !== null) { this.productId = +idParam }
    if (!isNaN(this.productId)) {
      this.productService.getDetailProduct(this.productId).subscribe({
        next: (apiResponse: ApiResponse) => {
          const response: any = apiResponse.data;
          const imagesFromApi: any[] = response.productImages ?? response.product_images ?? response.images ?? [];
          const finalImagesSource = (Array.isArray(imagesFromApi) && imagesFromApi.length > 0) ? imagesFromApi : (response.thumbnail ? [{ image_url: response.thumbnail }] : []);
          response.product_images = finalImagesSource.map((img: any) => {
            let filename = '';
            if (typeof img === 'string') {
              filename = img;
            } else {
              filename = img.image_url ?? img.imageUrl ?? img.filename ?? img.name ?? '';
            }
            const url = filename ? (/^https?:\/\//i.test(filename) ? filename : `${environment.apiBaseUrl}/products/images/${filename}`) : '';
            return { image_url: url };
          });
          response.productImages = response.product_images;
          console.log('Normalized product_images urls:', response.product_images.map((p: any) => p.image_url));
          this.product = response;
          this.showImage(0);
        },
        complete: () => { debugger },
        error: (error: HttpErrorResponse) => {
          this.toastService.showToast({
            error: error,
            defaultMsg: 'Lỗi tải chi tiết sản phẩm',
            title: 'Lỗi Sản Phẩm'
          });
        }
      });
    } else {
      this.toastService.showToast({
        error: null,
        defaultMsg: 'ID sản phẩm không hợp lệ',
        title: 'Lỗi Dữ Liệu'
      });
    }
  }

  showImage(index: number): void {
    if (this.product && this.product.product_images && this.product.product_images.length > 0) {
      if (index < 0) {
        index = 0;
      } else if (index >= this.product.product_images.length) {
        index = this.product.product_images.length - 1;
      }
      this.currentImageIndex = index;
    }
  }

  nextImage(): void {
    this.showImage(this.currentImageIndex + 1);
  }

  previousImage(): void {
    this.showImage(this.currentImageIndex - 1);
  }

  thumbnailClick(index: number) {
    this.currentImageIndex = index;
  }

  addToCart(): void {
    this.isPressedAddToCart = true;
    if (this.product) {
      this.cartService.addToCart(this.product.id, this.quantity);
    } else {
      console.error('Không thể thêm sản phẩm vào giỏ hàng vì product là null.');
    }
  }

}
