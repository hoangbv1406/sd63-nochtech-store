package com.project.shopapp.controllers;

import com.project.shopapp.components.SecurityUtils;
import com.project.shopapp.dtos.ProductReviewDTO;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.product.ProductReviewResponse;
import com.project.shopapp.services.product.review.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/reviews")
@RequiredArgsConstructor
public class ProductReviewController {
    private final ProductReviewService productReviewService;
    private final SecurityUtils securityUtils;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getReviewsByProduct(
            @RequestParam("productId") Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        Page<ProductReviewResponse> reviews = productReviewService.getReviewsByProduct(productId, pageRequest);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Reviews retrieved successfully.")
                .status(HttpStatus.OK)
                .data(reviews)
                .build()
        );
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject> createReview(
            @Valid @RequestBody ProductReviewDTO reviewDTO
    ) throws Exception {
        User loginUser = securityUtils.getLoggedInUser();
        if (!loginUser.getId().equals(reviewDTO.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseObject.builder()
                    .message("You cannot review as another user")
                    .status(HttpStatus.FORBIDDEN)
                    .build());
        }
        ProductReviewResponse response = productReviewService.createReview(reviewDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Review created successfully.")
                .status(HttpStatus.CREATED)
                .data(response)
                .build()
        );
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ResponseObject> updateReview(
            @PathVariable("reviewId") Long reviewId,
            @Valid @RequestBody ProductReviewDTO reviewDTO
    ) throws Exception {
        ProductReviewResponse response = productReviewService.updateReview(reviewId, reviewDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Review updated successfully")
                .status(HttpStatus.OK)
                .data(response)
                .build());
    }

}
