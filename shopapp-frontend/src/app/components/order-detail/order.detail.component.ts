// File: order.detail.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { OrderResponse } from '../../responses/order.response';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule, HeaderComponent, FooterComponent],
  templateUrl: './order.detail.component.html',
  styleUrl: './order.detail.component.scss'
})
export class OrderDetailComponent implements OnInit {
  orderResponse: OrderResponse = {
    id: 0,
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
    order_details: []
  };

  constructor(
    private orderService: OrderService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.getOrderDetails();
  }

  getOrderDetails(): void {
    const orderId = Number(this.route.snapshot.paramMap.get('id'));

    this.orderService.getOrderById(orderId).subscribe({
      next: (response: any) => {
        this.orderResponse = response.data;

        if (this.orderResponse.order_date && Array.isArray(this.orderResponse.order_date)) {
          const d = this.orderResponse.order_date;
          this.orderResponse.order_date = new Date(d[0], d[1] - 1, d[2], d[3], d[4]);
        }

        this.orderResponse.order_details = this.orderResponse.order_details.map((order_detail: any) => {
          order_detail.product = {
            id: order_detail.product_id,
            name: order_detail.product_name,
            thumbnail: order_detail.thumbnail,
            price: order_detail.price
          };

          if (order_detail.product.thumbnail && !order_detail.product.thumbnail.startsWith('http')) {
            order_detail.product.thumbnail = `${environment.apiBaseUrl}/products/images/${order_detail.product.thumbnail}`;
          }

          order_detail.number_of_products = order_detail.numberOfProducts || order_detail.number_of_products;
          return order_detail;
        });

      },
      error: (err) => {
        console.error('Lỗi tải đơn hàng:', err);
      }
    });
  }

  goBack() {
    this.router.navigate(['/order-history']);
  }

}