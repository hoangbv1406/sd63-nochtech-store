package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ProductReviewDTO {

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("content")
    private String content;

    @Min(value = 1, message = "Rating must be at least 1 star")
    @Max(value = 5, message = "Rating cannot exceed 5 stars")
    @JsonProperty("rating")
    private Integer rating;

    @JsonProperty("images")
    private List<String> images;

}
