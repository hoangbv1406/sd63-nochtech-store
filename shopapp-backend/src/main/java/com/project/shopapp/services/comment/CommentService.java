package com.project.shopapp.services.comment;

import com.project.shopapp.dtos.ProductReviewDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.ProductReview;
import com.project.shopapp.responses.comment.CommentResponse;

import java.util.List;

public interface CommentService {
    List<CommentResponse> getCommentsByUserAndProduct(Long userId, Long productId);
    List<CommentResponse> getCommentsByProduct(Long productId);
    ProductReview createComment(ProductReviewDTO comment);
    void updateComment(Long id, ProductReviewDTO commentDTO) throws DataNotFoundException;
}
