package com.project.shopapp.responses.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CartResponse {

    @JsonProperty("total_items")
    private Integer totalItems;

    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    @JsonProperty("cart_items")
    private List<CartItemResponse> cartItems;

}
