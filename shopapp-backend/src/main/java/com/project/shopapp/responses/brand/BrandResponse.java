package com.project.shopapp.responses.brand;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.Brand;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BrandResponse {

    private Long id;
    private String name;

    @JsonProperty("icon_url")
    private String iconUrl;

    public static BrandResponse fromBrand(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .iconUrl(brand.getIconUrl())
                .build();
    }

}
