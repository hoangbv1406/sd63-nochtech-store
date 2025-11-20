package com.project.shopapp.services.category;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import com.project.shopapp.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    @Transactional
    public Category createCategory(CategoryDTO categoryDTO) {
        String slug = categoryDTO.getSlug();
        if (slug == null || slug.isEmpty()) {
            slug = createSlug(categoryDTO.getName());
        }

        Category newCategory = Category.builder()
                .name(categoryDTO.getName())
                .slug(slug)
                .build();

        if (categoryDTO.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            newCategory.setParent(parent);
        }

        return categoryRepository.save(newCategory);
    }

    @Override
    @Transactional
    public Category updateCategory(long categoryId, CategoryDTO categoryDTO) {
        Category existingCategory = getCategoryById(categoryId);
        existingCategory.setName(categoryDTO.getName());

        if (categoryDTO.getSlug() != null && !categoryDTO.getSlug().isEmpty()) {
            existingCategory.setSlug(categoryDTO.getSlug());
        } else {
            existingCategory.setSlug(createSlug(categoryDTO.getName()));
        }

        if (categoryDTO.getParentId() != null) {
            if (categoryDTO.getParentId() == categoryId) {
                throw new RuntimeException("Category cannot be its own parent");
            }
            Category parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            existingCategory.setParent(parent);
        } else {
            existingCategory.setParent(null);
        }

        return categoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(long categoryId) {
        Category category = getCategoryById(categoryId);
        categoryRepository.delete(category);
    }

    private String createSlug(String input) {
        if (input == null) return "";
        String nowhitespace = Pattern.compile("[\\s]").matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").toLowerCase(Locale.ENGLISH);
    }

}
