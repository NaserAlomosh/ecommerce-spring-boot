package com.smart.ecommerce.storage;

import com.smart.ecommerce.config.FileStorageProperties;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(prefix = "app.file-storage", name = "type",
                       havingValue = "local", matchIfMissing = true)
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {
  private final FileStorageProperties properties;
  private final ImageFileValidator imageFileValidator;

  @Override
  public StoredFile storeProductImage(MultipartFile file) {
    return storeImage(file, "products");
  }

  @Override
  public StoredFile storeWebsiteImage(MultipartFile file) {
    return storeImage(file, "website");
  }

  private StoredFile storeImage(MultipartFile file, String directory) {
    String contentType = file.getContentType();
    String extension = imageFileValidator.validateAndGetExtension(file);
    Path root = root().resolve(directory).normalize();
    String fileName = UUID.randomUUID() + extension;
    Path destination = root.resolve(fileName).normalize();
    if (!destination.startsWith(root))
      throw new IllegalArgumentException("Invalid storage path");
    try {
      Files.createDirectories(root);
      file.transferTo(destination);
    } catch (IOException ex) {
      throw new IllegalStateException("Could not store image", ex);
    }
    return new StoredFile("/uploads/" + directory + "/" + fileName,
                          directory + "/" + fileName, contentType,
                          file.getSize());
  }

  @Override
  public void delete(String storagePath) {
    if (storagePath == null || storagePath.isBlank())
      return;
    Path root = root();
    Path target = root.resolve(storagePath).normalize();
    if (!target.startsWith(root))
      throw new IllegalArgumentException("Invalid storage path");
    try {
      Files.deleteIfExists(target);
    } catch (IOException ignored) {
    }
  }

  private Path root() {
    return Path.of(properties.getUploadDir()).toAbsolutePath().normalize();
  }
}
