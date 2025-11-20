package com.project.shopapp.services.product.review;

import com.project.shopapp.dtos.ProductReviewDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.responses.product.ProductReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductReviewService {
    ProductReviewResponse createReview(ProductReviewDTO reviewDTO) throws Exception;
    Page<ProductReviewResponse> getReviewsByProduct(Long productId, Pageable pageable);
    List<ProductReviewResponse> getReviewsByUser(Long userId);
    ProductReviewResponse updateReview(Long reviewId, ProductReviewDTO reviewDTO) throws DataNotFoundException;
    void deleteReview(Long reviewId);
}
