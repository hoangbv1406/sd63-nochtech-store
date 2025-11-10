// File: order.component.ts
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { BaseComponent } from '../base/base.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Product } from '../../models/product';
import { OrderDTO } from '../../dtos/order/order.dto';
import { ApiResponse } from '../../responses/api.response';
import { environment } from '../../../environments/environment';
import { HttpErrorResponse } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-order',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent, ReactiveFormsModule],
  templateUrl: './order.component.html',
  styleUrl: './order.component.scss'
})
export class OrderComponent extends BaseComponent implements OnInit {
  private formBuilder = inject(FormBuilder);

  orderForm: FormGroup;
  cartItems: { product: Product, quantity: number }[] = [];
  totalAmount: number = 0;
  couponDiscount: number = 0;
  couponApplied: boolean = false;
  cart: Map<number, number> = new Map();

  // Mặc định payment_method = 'vnpay'
  orderData: OrderDTO = {
    user_id: 0,
    fullname: '',
    email: '',
    phone_number: '',
    address: '',
    status: 'pending',
    note: '',
    total_money: 0,
    payment_method: 'vnpay',
    shipping_method: 'express',
    coupon_code: '',
    cart_items: []
  };

  constructor() {
    super();
    this.orderForm = this.formBuilder.group({
      fullname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone_number: ['', [Validators.required, Validators.minLength(6)]],
      address: ['', [Validators.required, Validators.minLength(5)]],
      note: [''],
      couponCode: [''],
      shipping_method: ['express'],
      payment_method: ['vnpay']
    });
  }

  ngOnInit(): void {
    this.orderData.user_id = this.tokenService.getUserId();
    this.prefillUserInfoToOrderForm();
    this.cart = this.cartService.getCart();
    const productIds = Array.from(this.cart.keys());

    if (productIds.length === 0) {
      return;
    }

    this.productService.getProductsByIds(productIds).subscribe({
      next: (apiResponse: ApiResponse) => {
        const products: Product[] = apiResponse.data || [];
        this.cartItems = productIds.map((id) => {
          const product = products.find((p) => p.id === id);
          if (product) {
            product.url = product.thumbnail;
          }
          return {
            product: product!,
            quantity: this.cart.get(id)!
          };
        });
      },
      complete: () => {
        this.calculateTotal();
      },
      error: (error: HttpErrorResponse) => {
        this.toastService.showToast({
          error: error,
          defaultMsg: 'Lỗi tải thông tin sản phẩm',
          title: 'Lỗi Giỏ Hàng'
        });
      }
    });
  }

  placeOrder() {
    if (this.orderForm.valid) {
      this.orderData = { ...this.orderData, ...this.orderForm.value };
      this.orderData.cart_items = this.cartItems.map(item => ({
        product_id: item.product.id,
        quantity: item.quantity
      }));
      this.orderData.total_money = this.totalAmount;
      if (this.orderData.payment_method === 'vnpay') {
        const amount = this.orderData.total_money || 0;
        this.paymentService.createPaymentUrl({ amount, language: 'vn' })
          .subscribe({
            next: (res: ApiResponse) => {
              const paymentUrl = res.data;
              const vnp_TxnRef = new URL(paymentUrl).searchParams.get('vnp_TxnRef') || '';
              this.orderService.placeOrder({
                ...this.orderData,
                vnp_txn_ref: vnp_TxnRef
              }).subscribe({
                next: (placeOrderResponse: ApiResponse) => {
                  window.location.href = paymentUrl;
                },
                error: (err: HttpErrorResponse) => {
                  this.toastService.showToast({
                    error: err,
                    defaultMsg: 'Lỗi trong quá trình đặt hàng',
                    title: 'Lỗi Đặt Hàng'
                  });
                }
              });
            },
            error: (err: HttpErrorResponse) => {
              this.toastService.showToast({
                error: err,
                defaultMsg: 'Lỗi kết nối đến cổng thanh toán',
                title: 'Lỗi Thanh Toán'
              });
            }
          });
      } else {
        this.orderService.placeOrder(this.orderData).subscribe({
          next: (response: ApiResponse) => {
            this.cartService.clearCart();
            this.router.navigate(['/']);
          },
          error: (err: HttpErrorResponse) => {
            this.toastService.showToast({
              error: err,
              defaultMsg: 'Lỗi trong quá trình đặt hàng',
              title: 'Lỗi Đặt Hàng'
            });
          }
        });
      }

    } else {
      this.toastService.showToast({
        error: 'Vui lòng điền đầy đủ thông tin bắt buộc',
        defaultMsg: 'Vui lòng điền đầy đủ thông tin bắt buộc',
        title: 'Lỗi Dữ Liệu'
      });
      this.orderForm.markAllAsTouched();
    }
  }

  decreaseQuantity(index: number): void {
    if (this.cartItems[index].quantity > 1) {
      this.cartItems[index].quantity--;
      this.updateCartFromCartItems();
      this.calculateTotal();
    }
  }

  increaseQuantity(index: number): void {
    this.cartItems[index].quantity++;
    this.updateCartFromCartItems();
    this.calculateTotal();
  }

  calculateTotal(): void {
    this.totalAmount = this.cartItems.reduce(
      (total, item) => total + item.product.price * item.quantity,
      0
    );
  }

  confirmDelete(index: number): void {
    if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
      this.cartItems.splice(index, 1);
      this.updateCartFromCartItems();
      this.calculateTotal();
    }
  }

  applyCoupon(): void {
    const couponCode = this.orderForm.get('couponCode')!.value;
    if (!this.couponApplied && couponCode) {
      this.calculateTotal();
      this.couponService.calculateCouponValue(couponCode, this.totalAmount)
        .subscribe({
          next: (apiResponse: ApiResponse) => {
            this.totalAmount = apiResponse.data;
            this.couponApplied = true;
          },
          error: (error) => {
            this.toastService.showToast({
              error: error,
              defaultMsg: 'Mã giảm giá không hợp lệ',
              title: 'Lỗi Coupon'
            });
          }
        });
    }
  }

  private updateCartFromCartItems(): void {
    this.cart.clear();
    this.cartItems.forEach(item => {
      this.cart.set(item.product.id, item.quantity);
    });
    this.cartService.setCart(this.cart);
  }

  private prefillUserInfoToOrderForm(): void {
    try {
      const raw = localStorage.getItem('user');
      if (raw) {
        const user = JSON.parse(raw);
        this.applyUserToForm(user);
        return;
      }
    } catch (e) { }
    const token = this.tokenService.getToken();
    if (!token) {
      return;
    }

    this.userService.getUserDetail(token).subscribe({
      next: (resp: any) => {
        const user = resp?.data;
        if (user) {
          try { localStorage.setItem('userResponse', JSON.stringify(user)); } catch (e) { }
          this.applyUserToForm(user);
        }
      },
      error: (err: HttpErrorResponse) => {
        console.warn('Không thể lấy chi tiết user để fill form order:', err?.message ?? err);
      }
    });
  }

  private applyUserToForm(user: any) {
    this.orderForm.patchValue({
      fullname: user.fullname ?? user.name ?? '',
      email: user.email ?? '',
      phone_number: user.phone_number ?? user.phone ?? '',
      address: user.address ?? ''
    });
    if (user.id) {
      this.orderData.user_id = user.id;
    }
  }
}
