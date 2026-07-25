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
  private final com.smart.ecommerce.util.MessageUtil messages;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<ProductResponse>
  createProduct(@Valid @RequestPart("product") ProductCreateRequest product,
                @RequestPart(value = "images",
                             required = false) List<MultipartFile> images) {
    return ApiResponse.success(messages.getMessage("admin.product_created"),
                               productService.create(product, images));
  }

  @PutMapping(value = "/{productId}",
              consumes = MediaType.APPLICATION_JSON_VALUE)
  public ApiResponse<ProductResponse>
  updateProduct(@PathVariable Long productId,
                @Valid @RequestBody ProductUpdateRequest product) {
    return ApiResponse.success(messages.getMessage("admin.product_updated"),
                               productService.update(productId, product));
  }

  @PutMapping(value = "/{productId}",
              consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<ProductResponse>
  updateProductWithImages(
      @PathVariable Long productId,
      @Valid @RequestPart("product") ProductUpdateRequest product,
      @RequestPart(value = "images",
                   required = false) List<MultipartFile> images) {
    return ApiResponse.success(
        messages.getMessage("admin.product_updated"),
        productService.update(productId, product, images));
  }

  @PostMapping(value = "/{productId}/images",
               consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<ProductResponse>
  uploadImages(@PathVariable Long productId,
               @RequestPart(value = "images",
                            required = false) List<MultipartFile> images) {
    return ApiResponse.success(
        messages.getMessage("admin.product_images_uploaded"),
        productService.uploadImages(productId, images));
  }

  @PutMapping(value = "/{productId}/images/{imageId}",
              consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<ProductResponse>
  replaceImage(@PathVariable Long productId, @PathVariable Long imageId,
               @RequestPart("image") MultipartFile image) {
    return ApiResponse.success(
        messages.getMessage("admin.product_image_replaced"),
        productService.replaceImage(productId, imageId, image));
  }

  @PatchMapping("/{productId}/images/{imageId}/primary")
  public ApiResponse<ProductResponse> setPrimary(@PathVariable Long productId,
                                                 @PathVariable Long imageId) {
    return ApiResponse.success(
        messages.getMessage("admin.product_primary_updated"),
        productService.setPrimary(productId, imageId));
  }

  @PatchMapping("/{productId}/images/reorder")
  public ApiResponse<ProductResponse>
  reorder(@PathVariable Long productId,
          @Valid @RequestBody ImageOrderRequest request) {
    return ApiResponse.success(
        messages.getMessage("admin.product_images_reordered"),
        productService.reorder(productId, request));
  }

  @DeleteMapping("/{productId}/images/{imageId}")
  public ApiResponse<Void> deleteImage(@PathVariable Long productId,
                                       @PathVariable Long imageId) {
    productService.deleteImage(productId, imageId);
    return ApiResponse.success(
        messages.getMessage("admin.product_image_deleted"), null);
  }
}
