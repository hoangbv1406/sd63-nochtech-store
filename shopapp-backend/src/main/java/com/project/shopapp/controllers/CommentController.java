package com.project.shopapp.controllers;

import com.project.shopapp.components.SecurityUtils;
import com.project.shopapp.dtos.ProductReviewDTO;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.comment.CommentResponse;
import com.project.shopapp.services.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final SecurityUtils securityUtils;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllComments(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam("productId") Long productId
    ) {
        List<CommentResponse> commentResponses;
        if (userId == null) {
            commentResponses = commentService.getCommentsByProduct(productId);
        } else {
            commentResponses = commentService.getCommentsByUserAndProduct(userId, productId);
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Comments retrieved successfully.")
                .status(HttpStatus.OK)
                .data(commentResponses)
                .build()
        );
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject> createComment(
            @Valid @RequestBody ProductReviewDTO commentDTO
    ) {
        User loginUser = securityUtils.getLoggedInUser();
        if (loginUser.getId() != commentDTO.getUserId()) {
            return ResponseEntity.badRequest().body(new ResponseObject(
                    "You cannot comment as another user",
                    HttpStatus.BAD_REQUEST,
                    null)
            );
        }
        commentService.createComment(commentDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Comment created successfully.")
                .status(HttpStatus.OK)
                .build()
        );
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ResponseObject> updateComment(
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody ProductReviewDTO commentDTO
    ) throws Exception {
        User loginUser = securityUtils.getLoggedInUser();
        if (!Objects.equals(loginUser.getId(), commentDTO.getUserId())) {
            return ResponseEntity.badRequest().body(new ResponseObject(
                    "You cannot update another user's comment",
                    HttpStatus.BAD_REQUEST,
                    null)
            );
        }
        commentService.updateComment(commentId, commentDTO);
        return ResponseEntity.ok(new ResponseObject(
                "Comment updated successfully. commentId = " + commentId,
                HttpStatus.OK,
                null)
        );
    }

    @PostMapping("/generateFakeComments")
    public ResponseEntity<String> generateFakeComments() {
        return ResponseEntity.ok("Fake comments generated successfully.");
    }

}
