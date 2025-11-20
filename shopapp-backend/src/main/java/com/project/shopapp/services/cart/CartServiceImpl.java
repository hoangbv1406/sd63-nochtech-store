package com.project.shopapp.services.cart;

import com.project.shopapp.dtos.CartItemDTO;
import com.project.shopapp.models.*;
import com.project.shopapp.repositories.*;
import com.project.shopapp.responses.cart.CartItemResponse;
import com.project.shopapp.responses.cart.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId).orElseThrow();
            Cart newCart = Cart.builder().user(user).build();
            return cartRepository.save(newCart);
        });
    }

    @Override
    @Transactional
    public void addToCart(Long userId, CartItemDTO cartItemDTO) {
        Cart cart = getCartByUserId(userId);
        Product product = productRepository.findById(cartItemDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductVariant variant = null;
        if (cartItemDTO.getVariantId() != null) {
            variant = productVariantRepository.findById(cartItemDTO.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found"));
        }

        CartItem existingItem = cartItemRepository.findByCartIdAndProductIdAndVariantId(
                cart.getId(), product.getId(), variant != null ? variant.getId() : null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + cartItemDTO.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .variant(variant)
                    .quantity(cartItemDTO.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
        }
    }

    @Override
    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    @Override
    public CartResponse getCartResponse(Long userId) {
        Cart cart = getCartByUserId(userId);
        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : cart.getCartItems()) {
            BigDecimal price = item.getVariant() != null ? item.getVariant().getPrice() : item.getProduct().getPrice();
            totalPrice = totalPrice.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
            totalItems += item.getQuantity();
        }

        return CartResponse.builder()
                .totalItems(totalItems)
                .totalPrice(totalPrice)
                .cartItems(cart.getCartItems().stream()
                        .map(CartItemResponse::fromCartItem)
                        .collect(Collectors.toList()))
                .build();
    }

}
