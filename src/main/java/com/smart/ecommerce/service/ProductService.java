package com.smart.ecommerce.service;

import com.smart.ecommerce.config.FileStorageProperties;
import com.smart.ecommerce.dto.category.CategoryDtos.CategorySummary;
import com.smart.ecommerce.dto.product.ProductDtos.*;
import com.smart.ecommerce.entity.*;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.repository.*;
import com.smart.ecommerce.storage.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository imageRepository;
    private final InventoryMovementRepository inventoryRepository;
    private final FileStorageService storageService;
    private final FileStorageProperties properties;

    @Transactional
    public ProductResponse create(ProductCreateRequest request, List<MultipartFile> images) {
        validatePrices(request.price(), request.discountPrice());
        List<StoredFile> stored = new ArrayList<>();
        try {
            Product product = new Product();
            map(request, product);
            Category category = categoryRepository.findById(request.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            if (!category.isActive()) throw new IllegalArgumentException("Product category must be active");
            product.setCategory(category);
            product = productRepository.save(product);
            InventoryMovement movement = new InventoryMovement();
            movement.setProduct(product); movement.setMovementType("INITIAL_STOCK"); movement.setQuantity(request.stockQuantity());
            inventoryRepository.save(movement);
            addImages(product, images, stored);
            return toResponse(productRepository.save(product));
        } catch (RuntimeException ex) {
            stored.forEach(f -> storageService.delete(f.storagePath()));
            throw ex;
        }
    }

    @Transactional
    public ProductResponse uploadImages(Long productId, List<MultipartFile> files) {
        Product product = findProduct(productId);
        List<StoredFile> stored = new ArrayList<>();
        try { addImages(product, files, stored); return toResponse(productRepository.save(product)); }
        catch (RuntimeException ex) { stored.forEach(f -> storageService.delete(f.storagePath())); throw ex; }
    }

    @Transactional
    public ProductResponse replaceImage(Long productId, Long imageId, MultipartFile file) {
        ProductImage image = findImage(productId, imageId);
        StoredFile stored = storageService.storeProductImage(file);
        deleteOnRollback(stored.storagePath());
        String oldPath = image.getStoragePath();
        try {
            image.setImageUrl(stored.imageUrl()); image.setStoragePath(stored.storagePath()); image.setContentType(stored.contentType()); image.setFileSize(stored.fileSize());
            imageRepository.save(image); deleteAfterCommit(oldPath); return toResponse(findProduct(productId));
        } catch (RuntimeException ex) { storageService.delete(stored.storagePath()); throw ex; }
    }

    @Transactional
    public ProductResponse setPrimary(Long productId, Long imageId) {
        ProductImage selected = findImage(productId, imageId);
        imageRepository.findByProductIdOrderBySortOrderAsc(productId).forEach(i -> i.setPrimaryImage(i.getId().equals(selected.getId())));
        return toResponse(findProduct(productId));
    }

    @Transactional
    public ProductResponse reorder(Long productId, ImageOrderRequest request) {
        List<ProductImage> images = imageRepository.findByProductIdOrderBySortOrderAsc(productId);
        Map<Long, ProductImage> byId = new HashMap<>(); images.forEach(i -> byId.put(i.getId(), i));
        for (int i = 0; i < request.imageIds().size(); i++) {
            ProductImage image = byId.get(request.imageIds().get(i));
            if (image == null) throw new ResourceNotFoundException("Product image not found");
            image.setSortOrder(i);
        }
        return toResponse(findProduct(productId));
    }

    @Transactional
    public void deleteImage(Long productId, Long imageId) {
        ProductImage image = findImage(productId, imageId);
        imageRepository.delete(image);
        deleteAfterCommit(image.getStoragePath());
    }

    private void addImages(Product product, List<MultipartFile> files, List<StoredFile> stored) {
        if (files == null || files.isEmpty()) return;
        if (imageRepository.countByProductId(product.getId()) + files.size() > properties.getMaxImagesPerProduct()) throw new IllegalArgumentException("Maximum images per product exceeded");
        int next = product.getImages().size();
        for (MultipartFile file : files) {
            StoredFile sf = storageService.storeProductImage(file); stored.add(sf); deleteOnRollback(sf.storagePath());
            ProductImage image = new ProductImage(); image.setProduct(product); image.setImageUrl(sf.imageUrl()); image.setStoragePath(sf.storagePath()); image.setContentType(sf.contentType()); image.setFileSize(sf.fileSize()); image.setSortOrder(next++); image.setPrimaryImage(product.getImages().isEmpty());
            product.getImages().add(image);
        }
    }

    private void deleteOnRollback(String path) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) return;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCompletion(int status) { if (status != STATUS_COMMITTED) storageService.delete(path); }
        });
    }

    private void deleteAfterCommit(String path) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) { storageService.delete(path); return; }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() { storageService.delete(path); }
        });
    }

    private Product findProduct(Long id) { return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found")); }
    private ProductImage findImage(Long productId, Long imageId) { ProductImage i = imageRepository.findById(imageId).orElseThrow(() -> new ResourceNotFoundException("Product image not found")); if (!i.getProduct().getId().equals(productId)) throw new ResourceNotFoundException("Product image not found"); return i; }
    private void map(ProductCreateRequest r, Product p) { p.setNameEn(r.nameEn()); p.setNameAr(r.nameAr()); p.setDescriptionEn(r.descriptionEn()); p.setDescriptionAr(r.descriptionAr()); p.setSku(r.sku()); p.setPrice(r.price()); p.setDiscountPrice(r.discountPrice()); p.setStockQuantity(r.stockQuantity()); p.setLowStockThreshold(r.lowStockThreshold()); p.setActive(r.active()); p.setFeatured(r.featured()); }
    private void validatePrices(BigDecimal price, BigDecimal discount) { if (discount != null && discount.compareTo(price) >= 0) throw new IllegalArgumentException("discountPrice must be lower than price"); }
    private ProductResponse toResponse(Product p) { BigDecimal eff = p.getDiscountPrice() == null ? p.getPrice() : p.getDiscountPrice(); BigDecimal pct = p.getDiscountPrice() == null ? BigDecimal.ZERO : p.getPrice().subtract(p.getDiscountPrice()).multiply(BigDecimal.valueOf(100)).divide(p.getPrice(), 2, RoundingMode.HALF_UP); Category category = p.getCategory(); return new ProductResponse(p.getId(), new CategorySummary(category.getId(), category.getNameEn(), category.getNameAr()), p.getNameEn(), p.getNameAr(), p.getSku(), p.getPrice(), p.getDiscountPrice(), eff, pct, p.getStockQuantity(), p.getLowStockThreshold(), p.getStockQuantity() > 0, p.getStockQuantity() <= p.getLowStockThreshold(), p.isActive(), p.isFeatured(), p.getImages().stream().map(i -> new ProductImageResponse(i.getId(), i.getImageUrl(), i.isPrimaryImage(), i.getSortOrder())).toList()); }
}
