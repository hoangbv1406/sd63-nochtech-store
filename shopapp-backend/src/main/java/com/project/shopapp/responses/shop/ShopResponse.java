package com.project.shopapp.responses.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.Shop;
import com.project.shopapp.shared.base.BaseResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopResponse extends BaseResponse {

    private Long id;

    private String name;

    private String slug;

    private String description;

    private String address;

    @JsonProperty("owner_id")
    private Long ownerId;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("banner_url")
    private String bannerUrl;

    @JsonProperty("rating_avg")
    private Float ratingAvg;

    @JsonProperty("total_orders")
    private Integer totalOrders;

    public static ShopResponse fromShop(Shop shop) {
        ShopResponse response = ShopResponse.builder()
                .id(shop.getId())
                .name(shop.getName())
                .slug(shop.getSlug())
                .description(shop.getDescription())
                .address(shop.getAddress())
                .ownerId(shop.getOwner() != null ? shop.getOwner().getId() : null)
                .logoUrl(shop.getLogoUrl())
                .bannerUrl(shop.getBannerUrl())
                .ratingAvg(shop.getRatingAvg())
                .totalOrders(shop.getTotalOrders())
                .build();

        response.setCreatedAt(shop.getCreatedAt());
        response.setUpdatedAt(shop.getUpdatedAt());
        return response;
    }

}
