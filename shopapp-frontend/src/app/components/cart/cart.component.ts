// File: cart.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { CartService } from '../../services/cart.service';

@Component({
    selector: 'app-cart',
    standalone: true,
    imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent],
    templateUrl: './cart.component.html',
    styleUrl: './cart.component.scss'
})
export class CartComponent implements OnInit {
    cartEntries: Array<{ productId: number; quantity: number }> = [];

    constructor(private cartService: CartService) { }

    ngOnInit(): void {
        this.loadCart();
    }

    private loadCart() {
        const map = this.cartService.getCart() ?? new Map<number, number>();
        this.cartEntries = Array.from(map.entries()).map(([id, qty]) => ({
            productId: Number(id),
            quantity: qty
        }));
    }

    increase(productId: number) {
        const map = this.cartService.getCart();
        const cur = map.get(productId) ?? 0;
        map.set(productId, cur + 1);
        this.cartService.setCart(map);
        this.loadCart();
    }

    decrease(productId: number) {
        const map = this.cartService.getCart();
        const cur = map.get(productId) ?? 0;
        if (cur <= 1) {
            map.delete(productId);
        } else {
            map.set(productId, cur - 1);
        }
        this.cartService.setCart(map);
        this.loadCart();
    }

    remove(productId: number) {
        const map = this.cartService.getCart();
        map.delete(productId);
        this.cartService.setCart(map);
        this.loadCart();
    }

    clearCart() {
        this.cartService.clearCart();
        this.loadCart();
    }

    getTotalItems(): number {
        return this.cartEntries.reduce((s, e) => s + e.quantity, 0);
    }

}
