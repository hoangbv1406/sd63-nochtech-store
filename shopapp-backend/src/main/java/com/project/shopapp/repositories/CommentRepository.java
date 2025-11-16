package com.project.shopapp.repositories;

import com.project.shopapp.models.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<ProductReview, Long> {
    List<ProductReview> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    List<ProductReview> findByProductId(@Param("productId") Long productId);
}
