package com.project.shopapp.controllers;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import com.project.shopapp.shared.base.ResponseObject;
import com.project.shopapp.services.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Categories retrieved successfully.")
                .status(HttpStatus.OK)
                .data(categories)
                .build()
        );
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ResponseObject> getCategoryById(@PathVariable("categoryId") Long categoryId) {
        Category existingCategory = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(existingCategory)
                .message("Category retrieved successfully. categoryId = " + categoryId)
                .status(HttpStatus.OK)
                .build()
        );
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject> createCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(errorMessages.toString())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build()
            );
        }
        Category category = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Category created successfully.")
                .status(HttpStatus.OK)
                .data(category)
                .build()
        );
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ResponseObject> updateCategory(
            @PathVariable("categoryId") Long categoryId,
            @Valid @RequestBody CategoryDTO categoryDTO
    ) {
        categoryService.updateCategory(categoryId, categoryDTO);
        return ResponseEntity.ok(ResponseObject
                .builder()
                .data(categoryService.getCategoryById(categoryId))
                .message("Category updated successfully. categoryId = " + categoryId)
                .build()
        );
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ResponseObject> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Category deleted successfully. categoryId = " + categoryId)
                .build()
        );
    }

}
