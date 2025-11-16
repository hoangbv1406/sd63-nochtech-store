package com.project.shopapp.responses.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.ProductReview;
import com.project.shopapp.responses.BaseResponse;
import com.project.shopapp.responses.user.UserResponse;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CommentResponse extends BaseResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("content")
    private String content;

    @JsonProperty("user")
    private UserResponse user;

    @JsonProperty("product_id")
    private Long productId;

    public static CommentResponse fromComment(ProductReview comment) {
        UserResponse userResponse = UserResponse.fromUser(comment.getUser());
        CommentResponse result = CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .user(userResponse)
                .productId(comment.getProduct().getId())
                .build();
        result.setCreatedAt(comment.getCreatedAt());
        return result;
    }

}
