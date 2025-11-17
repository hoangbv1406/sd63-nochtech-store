package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ProductImageDTO {

    @Min(value = 1, message = "Product ID must be at least 1")
    @JsonProperty("product_id")
    private Long productId;

    @Size(min = 5, max = 300, message = "Image URL must be between 5 and 300 characters")
    @JsonProperty("image_url")
    private String imageUrl;

}
