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
@RequestMapping("/api/v1/categories")
public class CategoryController {
  private final CategoryService categoryService;
  private final MessageUtil messages;

  @PostMapping
  public ApiResponse<CategoryResponse>
  create(@Valid @RequestBody CategoryRequest request) {
    return ApiResponse.success(messages.getMessage("category_created"),
                               categoryService.create(request));
  }

  @GetMapping
  public ApiResponse<List<CategoryResponse>> list() {
    return ApiResponse.success(messages.getMessage("categories"),
                               categoryService.list(true));
  }

  @GetMapping("/{categoryId}")
  public ApiResponse<CategoryResponse> get(@PathVariable Long categoryId) {
    return ApiResponse.success(messages.getMessage("category"),
                               categoryService.get(categoryId, true));
  }
}
