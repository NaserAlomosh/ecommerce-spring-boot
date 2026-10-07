package com.smart.ecommerce.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.smart.ecommerce.config.FileStorageProperties;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(prefix = "app.file-storage", name = "type",
                       havingValue = "cloudinary")
@RequiredArgsConstructor
public class CloudinaryFileStorageService implements FileStorageService {
  private final FileStorageProperties properties;
  private final ImageFileValidator imageFileValidator;

  @Override
  public StoredFile storeProductImage(MultipartFile file) {
    return storeImage(file, properties.getCloudinary().getFolder());
  }

  @Override
  public StoredFile storeWebsiteImage(MultipartFile file) {
    return storeImage(file, properties.getCloudinary().getFolder() + "/website");
  }

  private StoredFile storeImage(MultipartFile file, String folder) {
    String contentType = file.getContentType();
    imageFileValidator.validateAndGetExtension(file);

    String publicId = UUID.randomUUID().toString();
    try {
      Map<?, ?> result = cloudinary().uploader().upload(
          file.getBytes(),
          ObjectUtils.asMap("folder", folder, "public_id", publicId,
                            "resource_type", "image"));
      String secureUrl = (String)result.get("secure_url");
      String storedPublicId = (String)result.get("public_id");
      return new StoredFile(secureUrl, storedPublicId, contentType,
                            file.getSize());
    } catch (IOException ex) {
      throw new IllegalStateException("Could not upload image to Cloudinary",
                                      ex);
    }
  }

  @Override
  public void delete(String storagePath) {
    if (storagePath == null || storagePath.isBlank())
      return;
    try {
      cloudinary().uploader().destroy(storagePath, ObjectUtils.emptyMap());
    } catch (IOException ex) {
      throw new IllegalStateException("Could not delete image from Cloudinary",
                                      ex);
    }
  }

  private Cloudinary cloudinary() {
    FileStorageProperties.Cloudinary cloudinaryProperties =
        properties.getCloudinary();
    if (cloudinaryProperties.getCloudName().isBlank() ||
        cloudinaryProperties.getApiKey().isBlank() ||
        cloudinaryProperties.getApiSecret().isBlank()) {
      throw new IllegalStateException(
          "Cloudinary storage requires CLOUDINARY_CLOUD_NAME, " +
          "CLOUDINARY_API_KEY, and CLOUDINARY_API_SECRET");
    }
    return new Cloudinary(ObjectUtils.asMap(
        "cloud_name", cloudinaryProperties.getCloudName(), "api_key",
        cloudinaryProperties.getApiKey(), "api_secret",
        cloudinaryProperties.getApiSecret(), "secure", true));
  }
}
