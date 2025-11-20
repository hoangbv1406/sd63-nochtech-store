package com.project.shopapp.services.product.review;

import com.project.shopapp.dtos.ProductReviewDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductReview;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.ProductReviewRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.product.ProductReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProductReviewResponse createReview(ProductReviewDTO reviewDTO) throws Exception {
        Product product = productRepository.findById(reviewDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Product not found with id: " + reviewDTO.getProductId()));

        User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + reviewDTO.getUserId()));

        ProductReview newReview = ProductReview.builder()
                .user(user)
                .product(product)
                .content(reviewDTO.getContent())
                .rating(reviewDTO.getRating())
                .build();

        productReviewRepository.save(newReview);
        return ProductReviewResponse.fromProductReview(newReview);
    }

    @Override
    public Page<ProductReviewResponse> getReviewsByProduct(Long productId, Pageable pageable) {
        Page<ProductReview> reviewPage = productReviewRepository.findByProductId(productId, pageable);
        return reviewPage.map(ProductReviewResponse::fromProductReview);
    }

    @Override
    public List<ProductReviewResponse> getReviewsByUser(Long userId) {
        List<ProductReview> reviews = productReviewRepository.findByUserIdAndProductId(userId, null);
        return List.of();
    }

    @Override
    @Transactional
    public ProductReviewResponse updateReview(Long reviewId, ProductReviewDTO reviewDTO) throws DataNotFoundException {
        ProductReview existingReview = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new DataNotFoundException("Review not found"));

        if (reviewDTO.getContent() != null) {
            existingReview.setContent(reviewDTO.getContent());
        }
        if (reviewDTO.getRating() != null) {
            existingReview.setRating(reviewDTO.getRating());
        }

        productReviewRepository.save(existingReview);
        return ProductReviewResponse.fromProductReview(existingReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        if (!productReviewRepository.existsById(reviewId)) {
            return;
        }
        productReviewRepository.deleteById(reviewId);
    }

}
