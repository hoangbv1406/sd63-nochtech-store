package com.project.shopapp.responses.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.ProductReview;
import com.project.shopapp.models.Favorite;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.BaseResponse;
import com.project.shopapp.responses.comment.CommentResponse;
import com.project.shopapp.responses.favorite.FavoriteResponse;
import lombok.*;

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
    private String price;
    private String thumbnail;
    private String description;

    @JsonProperty("specs")
    private String specs;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("brand_id")
    private Long brandId;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("brand_name")
    private String brandName;

    @JsonProperty("product_images")
    private List<ProductImage> productImages = new ArrayList<>();

    @JsonProperty("variants")
    private List<ProductVariantResponse> variants = new ArrayList<>();

    public static ProductResponse fromProduct(Product product) {
        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice().toString())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .specs(product.getSpecs())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .productImages(product.getProductImages())
                .build();

        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        if (product.getVariants() != null) {
            response.setVariants(product.getVariants().stream()
                    .map(ProductVariantResponse::fromProductVariant)
                    .collect(Collectors.toList())
            );
        }

        return response;
    }

}
