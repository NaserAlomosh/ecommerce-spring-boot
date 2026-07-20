package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.category.CategoryDtos.*;
import com.smart.ecommerce.entity.Category;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.repository.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        validateUniqueNames(request, null);
        Category category = new Category();
        map(request, category);
        return toResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list(boolean activeOnly) {
        List<Category> categories = activeOnly
                ? categoryRepository.findByActiveTrue(Sort.by("nameEn").ascending())
                : categoryRepository.findAll(Sort.by("nameEn").ascending());
        return categories.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse get(Long id, boolean activeOnly) {
        Category category = find(id);
        if (activeOnly && !category.isActive()) throw new ResourceNotFoundException("Category not found");
        return toResponse(category);
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = find(id);
        validateUniqueNames(request, id);
        map(request, category);
        return toResponse(category);
    }

    @Transactional
    public void delete(Long id) {
        categoryRepository.delete(find(id));
    }

    private Category find(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    private void map(CategoryRequest request, Category category) {
        category.setNameEn(request.nameEn());
        category.setNameAr(request.nameAr());
        category.setActive(request.active());
    }

    private void validateUniqueNames(CategoryRequest request, Long currentId) {
        categoryRepository.findByNameEnIgnoreCase(request.nameEn())
                .filter(category -> !category.getId().equals(currentId))
                .ifPresent(category -> { throw new IllegalArgumentException("English category name already exists"); });
        categoryRepository.findByNameArIgnoreCase(request.nameAr())
                .filter(category -> !category.getId().equals(currentId))
                .ifPresent(category -> { throw new IllegalArgumentException("Arabic category name already exists"); });
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getNameEn(), category.getNameAr(), category.isActive());
    }
}
