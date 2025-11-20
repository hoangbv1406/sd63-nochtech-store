package com.project.shopapp.responses.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.CartItem;
import com.project.shopapp.responses.product.ProductVariantResponse;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CartItemResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("thumbnail")
    private String thumbnail;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    @JsonProperty("variant")
    private ProductVariantResponse variant;

    public static CartItemResponse fromCartItem(CartItem cartItem) {
        CartItemResponse response = CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .thumbnail(cartItem.getProduct().getThumbnail())
                .quantity(cartItem.getQuantity())
                .build();

        if (cartItem.getVariant() != null) {
            response.setVariant(ProductVariantResponse.fromProductVariant(cartItem.getVariant()));
            response.setUnitPrice(cartItem.getVariant().getPrice());
        } else {
            response.setUnitPrice(cartItem.getProduct().getPrice());
        }
        return response;
    }

}
