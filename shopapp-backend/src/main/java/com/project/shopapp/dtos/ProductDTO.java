package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.enums.ProductType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductDTO {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 3, max = 350, message = "Tên sản phẩm phải từ 3 đến 350 ký tự")
    private String name;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giá sản phẩm không được là số âm")
    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("thumbnail")
    private String thumbnail;

    @JsonProperty("description")
    private String description;

    @JsonProperty("specs")
    private String specs;

    @JsonProperty("is_imei_tracked")
    private Boolean isImeiTracked;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("brand_id")
    private Long brandId;

    @JsonProperty("product_type")
    private ProductType productType;

}
