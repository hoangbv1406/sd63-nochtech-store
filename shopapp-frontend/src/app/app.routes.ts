import { Routes } from '@angular/router';

import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { AuthCallbackComponent } from './components/auth-callback/auth-callback.component';
import { RegisterComponent } from './components/register/register.component';
import { DetailProductComponent } from './components/product-detail/product.detail.component';
import { OrderComponent } from './components/order/order.component';
import { OrderDetailComponent } from './components/order-detail/order.detail.component';
import { PaymentCallbackComponent } from './components/payment-callback/payment-callback.component';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'login', component: LoginComponent },
    { path: 'auth/google/callback', component: AuthCallbackComponent },
    { path: 'auth/facebook/callback', component: AuthCallbackComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'products/:id', component: DetailProductComponent },
    { path: 'orders', component: OrderComponent },
    { path: 'orders/:id', component: OrderDetailComponent },
    { path: 'payments/payment-callback', component: PaymentCallbackComponent },
];
