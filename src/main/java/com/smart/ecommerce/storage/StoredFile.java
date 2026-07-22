package com.smart.ecommerce.storage;

public record StoredFile(String imageUrl, String storagePath,
                         String contentType, long fileSize) {}
