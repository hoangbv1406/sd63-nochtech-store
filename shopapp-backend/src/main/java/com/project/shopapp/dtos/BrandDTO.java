package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class BrandDTO {

    @NotBlank(message = "Brand name is required")
    @JsonProperty("name")
    private String name;

    @JsonProperty("icon_url")
    private String iconUrl;

}
