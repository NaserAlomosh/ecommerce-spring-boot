package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.category.CategoryDtos.*;
import com.smart.ecommerce.service.CategoryService;
import com.smart.ecommerce.util.MessageUtil;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/categories")
public class AdminCategoryController {
  private final CategoryService categoryService;
  private final MessageUtil messages;

  @PostMapping
  public ApiResponse<CategoryResponse>
  create(@Valid @RequestBody CategoryRequest request) {
    return ApiResponse.success(messages.getMessage("admin.category_created"),
                               categoryService.create(request));
  }

  @GetMapping
  public ApiResponse<List<CategoryResponse>> list() {
    return ApiResponse.success(messages.getMessage("admin.categories"),
                               categoryService.list(false));
  }

  @GetMapping("/{categoryId}")
  public ApiResponse<CategoryResponse> get(@PathVariable Long categoryId) {
    return ApiResponse.success(messages.getMessage("admin.category"),
                               categoryService.get(categoryId, false));
  }

  @PutMapping("/{categoryId}")
  public ApiResponse<CategoryResponse>
  update(@PathVariable Long categoryId,
         @Valid @RequestBody CategoryRequest request) {
    return ApiResponse.success(messages.getMessage("admin.category_updated"),
                               categoryService.update(categoryId, request));
  }

  @DeleteMapping("/{categoryId}")
  public ApiResponse<Void> delete(@PathVariable Long categoryId) {
    categoryService.delete(categoryId);
    return ApiResponse.success(messages.getMessage("admin.category_deleted"),
                               null);
  }
}
