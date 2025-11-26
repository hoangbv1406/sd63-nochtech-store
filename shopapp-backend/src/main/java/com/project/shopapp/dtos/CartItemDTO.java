package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {

    @NotNull(message = "Product ID không được để trống")
    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("variant_id")
    private Long variantId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
    @JsonProperty("quantity")
    private Integer quantity;

}
