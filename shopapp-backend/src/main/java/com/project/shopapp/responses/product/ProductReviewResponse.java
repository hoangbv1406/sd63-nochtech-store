package com.project.shopapp.responses.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.ProductReview;
import com.project.shopapp.responses.BaseResponse;
import com.project.shopapp.responses.user.UserResponse;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductReviewResponse extends BaseResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("content")
    private String content;

    @JsonProperty("rating")
    private float rating;

    @JsonProperty("user")
    private UserResponse user;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("images")
    private List<String> images = new ArrayList<>();

    public static ProductReviewResponse fromProductReview(ProductReview review) {
        ProductReviewResponse response = ProductReviewResponse.builder()
                .id(review.getId())
                .content(review.getContent())
                .rating(review.getRating())
                .user(UserResponse.fromUser(review.getUser()))
                .productId(review.getProduct().getId())
                .build();
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }

}
