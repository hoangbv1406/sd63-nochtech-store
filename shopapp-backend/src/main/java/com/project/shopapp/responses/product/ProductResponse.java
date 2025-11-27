package com.project.shopapp.responses.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.BaseResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductResponse extends BaseResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private String thumbnail;
    private String description;
    private String slug;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("shop_id")
    private Long shopId;

    @JsonProperty("shop_name")
    private String shopName;

    @JsonProperty("brand_name")
    private String brandName;

    @JsonProperty("specs")
    private String specs;

    @JsonProperty("product_type")
    private String productType;

    @JsonProperty("rating_avg")
    private Float ratingAvg;

    @JsonProperty("review_count")
    private Integer reviewCount;

    @JsonProperty("product_images")
    @Builder.Default
    private List<ProductImage> productImages = new ArrayList<>();

    @JsonProperty("variants")
    @Builder.Default
    private List<ProductVariantResponse> variants = new ArrayList<>();

    public static ProductResponse fromProduct(Product product) {
        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .slug(product.getSlug())
                .specs(product.getSpecs())
                .productType(product.getProductType() != null ? product.getProductType().name() : null)
                .ratingAvg(product.getRatingAvg())
                .reviewCount(product.getReviewCount())
                .productImages(product.getProductImages())
                .build();

        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
        }
        if (product.getShop() != null) {
            response.setShopId(product.getShop().getId());
            response.setShopName(product.getShop().getName());
        }
        if (product.getBrand() != null) {
            response.setBrandName(product.getBrand().getName());
        }

        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            response.setVariants(product.getVariants().stream()
                    .map(ProductVariantResponse::fromProductVariant)
                    .collect(Collectors.toList())
            );
        }

        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }

}
