package com.project.shopapp.services.comment;

import com.project.shopapp.dtos.ProductReviewDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.ProductReview;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.ProductReviewRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.product.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final ProductReviewRepository commentRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public List<CommentResponse> getCommentsByUserAndProduct(Long userId, Long productId) {
        List<ProductReview> comments = commentRepository.findByUserIdAndProductId(userId, productId);
        return comments.stream().map(comment -> CommentResponse.fromComment(comment)).collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getCommentsByProduct(Long productId) {
        List<ProductReview> comments = commentRepository.findByProductId(productId);
        return comments.stream().map(comment -> CommentResponse.fromComment(comment)).collect(Collectors.toList());
    }

    @Override
    public ProductReview createComment(ProductReviewDTO commentDTO) {
        User user = userRepository.findById(commentDTO.getUserId()).orElse(null);
        Product product = productRepository.findById(commentDTO.getProductId()).orElse(null);
        if (user == null || product == null) {
            throw new IllegalArgumentException("User or product not found");
        }
        ProductReview newComment = ProductReview.builder().user(user).product(product).content(commentDTO.getContent()).build();
        return commentRepository.save(newComment);
    }

    @Override
    public void updateComment(Long id, ProductReviewDTO commentDTO) throws DataNotFoundException {
        ProductReview existingComment = commentRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Comment not found"));
        existingComment.setContent(commentDTO.getContent());
        commentRepository.save(existingComment);
    }

}
