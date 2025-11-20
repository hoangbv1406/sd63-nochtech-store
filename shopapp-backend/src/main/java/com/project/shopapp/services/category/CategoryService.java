package com.project.shopapp.services.category;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(long id);
    Category createCategory(CategoryDTO categoryDTO);
    Category updateCategory(long categoryId, CategoryDTO categoryDTO);
    void deleteCategory(long categoryId);
}
