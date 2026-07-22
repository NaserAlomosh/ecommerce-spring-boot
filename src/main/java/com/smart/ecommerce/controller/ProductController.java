package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.PaginationResponse;
import com.smart.ecommerce.dto.product.ProductDtos.ProductResponse;
import com.smart.ecommerce.service.ProductService;
import com.smart.ecommerce.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;
    private final MessageUtil messages;

    @GetMapping
    public ApiResponse<PaginationResponse<ProductResponse>> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean featured,
            Pageable pageable) {
        return ApiResponse.success(messages.getMessage("products"), productService.listPublic(categoryId, featured, pageable));
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> get(@PathVariable Long productId) {
        return ApiResponse.success(messages.getMessage("product"), productService.getPublic(productId));
    }
}
