package com.smart.ecommerce.controller;

import com.smart.ecommerce.dto.ApiResponse;
import com.smart.ecommerce.dto.product.ProductDtos.*;
import com.smart.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/products")
public class AdminProductController {
    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponse> createProduct(
            @Valid @RequestPart("product") ProductCreateRequest product,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ApiResponse.success("Product created", productService.create(product, images));
    }

    @PostMapping(value = "/{productId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponse> uploadImages(
            @PathVariable Long productId,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ApiResponse.success("Product images uploaded", productService.uploadImages(productId, images));
    }

    @PutMapping(value = "/{productId}/images/{imageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponse> replaceImage(
            @PathVariable Long productId,
            @PathVariable Long imageId,
            @RequestPart("image") MultipartFile image) {
        return ApiResponse.success("Product image replaced", productService.replaceImage(productId, imageId, image));
    }

    @PatchMapping("/{productId}/images/{imageId}/primary")
    public ApiResponse<ProductResponse> setPrimary(@PathVariable Long productId, @PathVariable Long imageId) {
        return ApiResponse.success("Primary image updated", productService.setPrimary(productId, imageId));
    }

    @PatchMapping("/{productId}/images/reorder")
    public ApiResponse<ProductResponse> reorder(@PathVariable Long productId, @Valid @RequestBody ImageOrderRequest request) {
        return ApiResponse.success("Product images reordered", productService.reorder(productId, request));
    }

    @DeleteMapping("/{productId}/images/{imageId}")
    public ApiResponse<Void> deleteImage(@PathVariable Long productId, @PathVariable Long imageId) {
        productService.deleteImage(productId, imageId);
        return ApiResponse.success("Product image deleted", null);
    }
}
