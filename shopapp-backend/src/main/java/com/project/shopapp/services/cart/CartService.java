package com.project.shopapp.services.cart;

import com.project.shopapp.dtos.CartItemDTO;
import com.project.shopapp.models.Cart;
import com.project.shopapp.responses.cart.CartResponse;

public interface CartService {
    Cart getCartByUserId(Long userId);
    void addToCart(Long userId, CartItemDTO cartItemDTO);
    void removeFromCart(Long userId, Long cartItemId);
    void clearCart(Long userId);
    CartResponse getCartResponse(Long userId);
}
