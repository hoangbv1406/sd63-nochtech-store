package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ProductDTO {

    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 350, message = "Product name must be between 3 and 350 characters")
    @JsonProperty("name")
    private String name;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
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

    @JsonProperty("variants")
    private List<ProductVariantDTO> variants;

    // --- INNER CLASS ĐỂ TẠO BIẾN THỂ ---
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductVariantDTO {
        @JsonProperty("sku")
        private String sku;

        @JsonProperty("price")
        private BigDecimal price;

        @JsonProperty("original_price")
        private BigDecimal originalPrice;

        @JsonProperty("image_url")
        private String imageUrl;

        @JsonProperty("options")
        private List<VariantOptionDTO> options;
    }

    // --- INNER CLASS ĐỂ CHỌN THUỘC TÍNH ---
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VariantOptionDTO {
        @JsonProperty("option_id")
        private Long optionId;

        @JsonProperty("value_id")
        private Long valueId;
    }

}
