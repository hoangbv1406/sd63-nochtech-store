import { Component, OnInit } from '@angular/core';
import { Product } from '../../../../models/product';
import { Category } from '../../../../models/category';
import { environment } from '../../../../../environments/environment';
import { ProductImage } from '../../../../models/product.image';
import { UpdateProductDTO } from '../../../../dtos/product/update.product.dto';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiResponse } from '../../../../responses/api.response';
import { HttpErrorResponse } from '@angular/common/http';
import { BaseComponent } from '../../../base/base.component';

@Component({
  selector: 'app-detail.product.admin',
  standalone: true,
  templateUrl: './update.product.admin.component.html',
  styleUrls: ['./update.product.admin.component.scss'],
  imports: [CommonModule, FormsModule]
})
export class UpdateProductAdminComponent extends BaseComponent implements OnInit {
  categories: Category[] = [];
  productId: number = 0;
  product: Product = {} as Product;
  updatedProduct: Product = {} as Product;
  images: File[] = [];

  ngOnInit(): void {
    this.activatedRoute.paramMap.subscribe(params => {
      this.productId = Number(params.get('id'));
      this.getProductDetails();
    });
    this.getCategories(1, 100);
  }

  getCategories(page: number, limit: number) {
    this.categoryService.getCategories(page, limit).subscribe({
      next: (apiResponse: ApiResponse) => {
        this.categories = apiResponse.data;
      },
      complete: () => { debugger },
      error: (error: HttpErrorResponse) => {
        this.toastService.showToast({
          error: error,
          defaultMsg: 'Lỗi tải danh mục',
          title: 'Lỗi Tải Dữ Liệu'
        });
      }
    });
  }

  getProductDetails(): void {
    this.productService.getDetailProduct(this.productId).subscribe({
      next: (apiResponse: ApiResponse) => {
        const response: any = apiResponse.data ?? {};
        const imagesFromApi: any[] = response.productImages ?? response.product_images ?? response.images ?? [];
        const finalImagesSource = (Array.isArray(imagesFromApi) && imagesFromApi.length > 0) ? imagesFromApi : (response.thumbnail ? [{ image_url: response.thumbnail }] : []);
        const normalized = finalImagesSource.map((img: any) => {
          let filename = '';
          if (typeof img === 'string') {
            filename = img;
          } else {
            filename = img.image_url ?? img.imageUrl ?? img.filename ?? img.name ?? '';
          }
          const url = filename ? (/^https?:\/\//i.test(filename) ? filename : `${environment.apiBaseUrl}/products/images/${filename}`) : '';
          return { ...img, image_url: url };
        });
        response.product_images = normalized;
        response.productImages = normalized;
        this.product = response as Product;
        this.updatedProduct = { ...response } as Product;
        this.updatedProduct.product_images = normalized.map(i => ({ ...i }));
        this.product.product_images = normalized.map(i => ({ ...i }));
        console.log('Normalized product_images urls (admin):', normalized.map(p => p.image_url));
      },
      error: (error: HttpErrorResponse) => {
        this.toastService.showToast({
          error: error,
          defaultMsg: 'Lỗi tải chi tiết sản phẩm',
          title: 'Lỗi Hệ Thống'
        });
      }
    });
  }

  updateProduct() {
    const updateProductDTO: UpdateProductDTO = {
      name: this.updatedProduct.name,
      price: this.updatedProduct.price,
      description: this.updatedProduct.description,
      category_id: this.updatedProduct.category_id
    };
    this.productService.updateProduct(this.product.id, updateProductDTO).subscribe({
      next: (apiResponse: ApiResponse) => { debugger },
      complete: () => { this.router.navigate(['/admin/products']) },
      error: (error: HttpErrorResponse) => {
        this.toastService.showToast({
          error: error,
          defaultMsg: 'Lỗi cập nhật sản phẩm',
          title: 'Lỗi Cập Nhật'
        });
      }
    });
  }

  onFileChange(event: any) {
    const files: FileList = event.target.files;
    if (!files || files.length === 0) return;
    const maxFiles = 10;
    if (files.length > maxFiles) {
      console.error(`Please select a maximum of ${maxFiles} images.`);
      return;
    }
    this.images = Array.from(files);
    this.productService.uploadImages(this.productId, this.images).subscribe({
      next: (apiResponse: ApiResponse) => {
        console.log('Images uploaded successfully:', apiResponse);
        this.images = [];
        this.getProductDetails();
      },
      error: (error: HttpErrorResponse) => {
        this.toastService.showToast({
          error: error,
          defaultMsg: 'Lỗi upload ảnh sản phẩm',
          title: 'Lỗi Upload'
        });
      }
    });
  }

  deleteImage(productImage: ProductImage) {
    if (!confirm('Are you sure you want to remove this image?')) return;
    this.productService.deleteProductImage(productImage.id).subscribe({
      next: () => {
        this.product.product_images = this.product.product_images.filter(pi => pi.id !== productImage.id);
        this.updatedProduct.product_images = this.updatedProduct.product_images?.filter(pi => pi.id !== productImage.id) ?? [];
        this.toastService.showToast({ defaultMsg: 'Xóa ảnh thành công', title: 'Thành Công' });
      },
      error: (error: HttpErrorResponse) => {
        this.toastService.showToast({
          error: error,
          defaultMsg: 'Lỗi xóa ảnh sản phẩm',
          title: 'Lỗi Xóa'
        });
      }
    });
  }

}
