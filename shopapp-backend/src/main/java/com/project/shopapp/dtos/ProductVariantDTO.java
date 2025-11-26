package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDTO {

    private String sku;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giá không được là số âm")
    private BigDecimal price;

    @JsonProperty("original_price")
    private BigDecimal originalPrice;

    @JsonProperty("image_url")
    private String imageUrl;

    @Min(value = 0, message = "Tồn kho không được âm")
    private Integer quantity;

    private BigDecimal weight;

    private String dimensions;

    @JsonProperty("option_value_ids")
    private List<Long> optionValueIds;

}
