import { Routes } from '@angular/router';

import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { AuthCallbackComponent } from './components/auth-callback/auth-callback.component';
import { RegisterComponent } from './components/register/register.component';
import { DetailProductComponent } from './components/product-detail/product.detail.component';
import { OrderComponent } from './components/order/order.component';
import { OrderDetailComponent } from './components/order-detail/order.detail.component';
import { PaymentCallbackComponent } from './components/payment-callback/payment-callback.component';
import { AdminComponent } from './components/admin/admin.component';
import { OrderAdminComponent } from './components/admin/order/order.admin.component';
import { ProductAdminComponent } from './components/admin/product/product.admin.component';
import { CategoryAdminComponent } from './components/admin/category/category.admin.component';
import { DetailOrderAdminComponent } from './components/admin/order-detail/detail.order.admin.component';
import { UserAdminComponent } from './components/admin/user/user.admin.component';
import { AdminGuardFn } from './guards/admin.guard';
import { UserProfileComponent } from './components/user-profile/user.profile.component';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'login', component: LoginComponent },
    { path: 'auth/google/callback', component: AuthCallbackComponent },
    { path: 'auth/facebook/callback', component: AuthCallbackComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'products/:id', component: DetailProductComponent },
    { path: 'orders', component: OrderComponent },
    { path: 'orders/:id', component: OrderDetailComponent },
    { path: 'user-profile', component: UserProfileComponent },
    { path: 'payments/payment-callback', component: PaymentCallbackComponent },

    {
        path: 'admin', component: AdminComponent, canActivate: [AdminGuardFn],
        children: [
            { path: 'categories', component: CategoryAdminComponent },
            { path: 'products', component: ProductAdminComponent },
            { path: 'orders', component: OrderAdminComponent },
            { path: 'orders/:id', component: DetailOrderAdminComponent },
            { path: 'users', component: UserAdminComponent },
        ]
    },
];
