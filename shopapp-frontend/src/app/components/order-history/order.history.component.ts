// File: order.history.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { OrderResponse } from '../../responses/order.response';
import { OrderService } from '../../services/order.service';
import { TokenService } from '../../services/token.service';
import { HttpErrorResponse } from '@angular/common/http';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';

@Component({
    selector: 'app-order-history',
    standalone: true,
    imports: [CommonModule, RouterModule, FooterComponent, HeaderComponent],
    templateUrl: './order.history.component.html',
    styleUrl: './order.history.component.scss'
})
export class OrderHistoryComponent implements OnInit {
    orders: OrderResponse[] = [];
    userId: number = 0;

    constructor(
        private orderService: OrderService,
        private tokenService: TokenService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.userId = this.tokenService.getUserId();
        if (this.userId === 0) {
            this.router.navigate(['/login']);
            return;
        }
        this.getOrders();
    }

    getOrders() {
        this.orderService.getOrdersByUserId(this.userId).subscribe({
            next: (response: any) => {
                this.orders = response.data;
            },
            error: (err: HttpErrorResponse) => {
                console.error('Lỗi lấy danh sách đơn hàng:', err);
            }
        });
    }

    viewOrderDetails(orderId: number) {
        this.router.navigate(['/orders', orderId]);
    }

}