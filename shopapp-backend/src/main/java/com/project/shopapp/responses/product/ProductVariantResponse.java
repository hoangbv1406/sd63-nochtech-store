package com.project.shopapp.responses.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.ProductVariant;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantResponse {

    private Long id;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("sku")
    private String sku;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("original_price")
    private BigDecimal originalPrice;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("attributes")
    private List<VariantAttributeResponse> attributes;

    public static ProductVariantResponse fromProductVariant(ProductVariant variant) {
        return ProductVariantResponse.builder()
                .id(variant.getId())
                .productId(variant.getProduct().getId())
                .sku(variant.getSku())
                .price(variant.getPrice())
                .originalPrice(variant.getOriginalPrice())
                .imageUrl(variant.getImageUrl())
                .quantity(variant.getQuantity())
                .attributes(variant.getOptionValues().stream()
                        .map(optionValue -> new VariantAttributeResponse(
                                optionValue.getOption().getName(),
                                optionValue.getValue())
                        )
                        .collect(Collectors.toList()))
                .build();
    }

    @Getter
    @AllArgsConstructor
    public static class VariantAttributeResponse {
        private String name;
        private String value;
    }

}
