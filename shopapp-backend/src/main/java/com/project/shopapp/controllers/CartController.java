package com.project.shopapp.controllers;

import com.project.shopapp.components.SecurityUtils;
import com.project.shopapp.dtos.CartItemDTO;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.cart.CartResponse;
import com.project.shopapp.services.cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final SecurityUtils securityUtils;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getCart() throws Exception {
        User user = securityUtils.getLoggedInUser();
        CartResponse cartResponse = cartService.getCartResponse(user.getId());
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Get cart successfully")
                .data(cartResponse)
                .build());
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseObject> addToCart(@Valid @RequestBody CartItemDTO cartItemDTO) throws Exception {
        User user = securityUtils.getLoggedInUser();
        cartService.addToCart(user.getId(), cartItemDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Added to cart successfully")
                .data(cartService.getCartResponse(user.getId()))
                .build());
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<ResponseObject> removeFromCart(@PathVariable Long cartItemId) throws Exception {
        User user = securityUtils.getLoggedInUser();
        cartService.removeFromCart(user.getId(), cartItemId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Removed item from cart")
                .data(cartService.getCartResponse(user.getId()))
                .build());
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ResponseObject> clearCart() throws Exception {
        User user = securityUtils.getLoggedInUser();
        cartService.clearCart(user.getId());
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Cart cleared")
                .build());
    }

}
