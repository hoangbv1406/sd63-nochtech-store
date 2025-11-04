// File: cart.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { forkJoin } from 'rxjs';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { BaseComponent } from '../base/base.component';
import { Product } from '../../models/product';
import { environment } from '../../../environments/environment';

@Component({
    selector: 'app-cart',
    standalone: true,
    imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent],
    templateUrl: './cart.component.html',
    styleUrl: './cart.component.scss'
})
export class CartComponent extends BaseComponent implements OnInit {
    cartEntries: Array<{ product: Product; quantity: number }> = [];
    totalAmount: number = 0;
    product?: Product;
    quantity: number = 1;
    isPressedAddToCart: boolean = false;

    ngOnInit(): void {
        this.loadCart();
    }

    private loadCart() {
        const cartMap = this.cartService.getCart() ?? new Map<number, number>();
        if (cartMap.size === 0) {
            this.cartEntries = [];
            return;
        }

        const productIds = Array.from(cartMap.keys());
        const requests = productIds.map(id => this.productService.getDetailProduct(id));

        forkJoin(requests).subscribe({
            next: (responses) => {
                this.cartEntries = responses.map((response) => {
                    const product = response.data;
                    if (product.thumbnail) {
                        product.url = product.thumbnail;
                    }
                    return {
                        product: product,
                        quantity: cartMap.get(product.id) || 1
                    };
                });
                this.calculateTotal();
            },
            error: (err) => console.error(err)
        });
    }

    onProductClick(productId: number) {
        this.router.navigate(['/products', productId]);
    }

    increase(productId: number) {
        const map = this.cartService.getCart();
        const cur = map.get(productId) ?? 0;
        map.set(productId, cur + 1);
        this.cartService.setCart(map);
        this.updateQuantityLocal(productId, cur + 1);
    }

    decrease(productId: number) {
        const map = this.cartService.getCart();
        const cur = map.get(productId) ?? 0;
        if (cur <= 1) {
            map.delete(productId);
            this.cartService.setCart(map);
            this.cartEntries = this.cartEntries.filter(e => e.product.id !== productId);
        } else {
            map.set(productId, cur - 1);
            this.cartService.setCart(map);
            this.updateQuantityLocal(productId, cur - 1);
        }
        this.calculateTotal();
    }

    remove(productId: number) {
        const map = this.cartService.getCart();
        map.delete(productId);
        this.cartService.setCart(map);
        this.cartEntries = this.cartEntries.filter(e => e.product.id !== productId);
        this.calculateTotal();
    }

    clearCart() {
        this.cartService.clearCart();
        this.cartEntries = [];
        this.totalAmount = 0;
    }

    getTotalItems(): number {
        return this.cartEntries.reduce((s, e) => s + e.quantity, 0);
    }

    calculateTotal(): void {
        this.totalAmount = this.cartEntries.reduce((acc, item) => acc + (item.product.price * item.quantity), 0);
    }

    private updateQuantityLocal(productId: number, newQty: number) {
        const item = this.cartEntries.find(e => e.product.id === productId);
        if (item) {
            item.quantity = newQty;
            this.calculateTotal();
        }
    }

    addToCart(): void {
        this.isPressedAddToCart = true;
        if (this.product) {
            this.cartService.addToCart(this.product.id, this.quantity);
        } else {
            console.error('Không thể thêm sản phẩm vào giỏ hàng vì product là null.');
        }
    }

    buyNow(): void {
        if (this.isPressedAddToCart == false) {
            this.addToCart();
        }
        this.router.navigate(['/orders']);
    }

}
