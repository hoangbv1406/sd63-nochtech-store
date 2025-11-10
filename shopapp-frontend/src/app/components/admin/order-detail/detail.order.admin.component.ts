// File: detail.order.admin.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BaseComponent } from '../../base/base.component';
import { OrderResponse } from '../../../responses/order.response';
import { ApiResponse } from '../../../responses/api.response';
import { environment } from '../../../../environments/environment';
import { HttpErrorResponse } from '@angular/common/http';
import { OrderDTO } from '../../../dtos/order/order.dto';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-detail-order-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './detail.order.admin.component.html',
  styleUrl: './detail.order.admin.component.scss'
})
export class DetailOrderAdminComponent extends BaseComponent implements OnInit {
  orderId: number = 0;
  orderResponse: OrderResponse = {
    id: 0, // Hoặc bất kỳ giá trị số nào bạn muốn
    user_id: 0,
    fullname: '',
    phone_number: '',
    email: '',
    address: '',
    note: '',
    order_date: new Date(),
    status: '',
    total_money: 0,
    shipping_method: '',
    shipping_address: '',
    shipping_date: new Date(),
    payment_method: '',
    order_details: [],

  };

  ngOnInit(): void {
    this.getOrderDetails();
  }

  getOrderDetails(): void {
    this.orderId = Number(this.activatedRoute.snapshot.paramMap.get('id'));
    this.orderService.getOrderById(this.orderId).subscribe({
      next: (apiResponse: ApiResponse) => {
        const response = apiResponse.data;
        this.orderResponse.id = response.id;
        this.orderResponse.user_id = response.user_id;
        this.orderResponse.fullname = response.fullname;
        this.orderResponse.email = response.email;
        this.orderResponse.phone_number = response.phone_number;
        this.orderResponse.address = response.address;
        this.orderResponse.note = response.note;
        this.orderResponse.total_money = response.total_money;

        if (response.order_date) {
          this.orderResponse.order_date = new Date(response.order_date);
        }

        if (response.shipping_date) {
          this.orderResponse.shipping_date = new Date(response.shipping_date);
        }

        this.orderResponse.order_details = response.order_details.map((order_detail: any) => {
          order_detail.product = {
            id: order_detail.product_id,
            name: order_detail.product_name,
            thumbnail: order_detail.thumbnail,
            price: order_detail.price
          };

          if (order_detail.product.thumbnail &&
            !order_detail.product.thumbnail.startsWith('http')) {
            order_detail.product.thumbnail = `${environment.apiBaseUrl}/products/images/${order_detail.product.thumbnail}`;
          }

          order_detail.number_of_products = order_detail.numberOfProducts || order_detail.number_of_products;

          return order_detail;
        });

        this.orderResponse.payment_method = response.payment_method;

        if (response.shipping_date) {
          this.orderResponse.shipping_date = new Date(
            response.shipping_date[0],
            response.shipping_date[1] - 1,
            response.shipping_date[2]
          );
        }

        this.orderResponse.shipping_method = response.shipping_method;
        this.orderResponse.status = response.status;
      },
      complete: () => {
        debugger;
      },
      error: (error: HttpErrorResponse) => {
        this.toastService.showToast({
          error: error,
          defaultMsg: 'Lỗi tải chi tiết đơn hàng',
          title: 'Lỗi Đơn Hàng'
        });
      }
    });
  }

  saveOrder(): void {
    debugger
    this.orderService.updateOrder(this.orderId, new OrderDTO(this.orderResponse))
      .subscribe({
        next: (response: ApiResponse) => {
          this.router.navigate(['../'], { relativeTo: this.activatedRoute });
        },
        complete: () => {
          debugger;
        },
        error: (error: HttpErrorResponse) => {
          this.toastService.showToast({
            error: error,
            defaultMsg: 'Lỗi cập nhật đơn hàng',
            title: 'Lỗi Hệ Thống'
          });
          this.router.navigate(['../'], { relativeTo: this.activatedRoute });
        }
      });
  }

}
