package com.smart.ecommerce.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    StoredFile storeProductImage(MultipartFile file);
    void delete(String storagePath);
}
