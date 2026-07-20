package com.smart.ecommerce.storage;

import com.smart.ecommerce.config.FileStorageProperties;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", ".jpg", "image/png", ".png", "image/webp", ".webp");
    private final FileStorageProperties properties;

    @Override
    public StoredFile storeProductImage(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Image file must not be empty");
        if (file.getSize() > properties.getMaxImageSize().toBytes()) throw new IllegalArgumentException("Image file exceeds maximum size");
        String contentType = file.getContentType();
        String extension = EXTENSIONS.get(contentType);
        if (extension == null) throw new IllegalArgumentException("Unsupported image content type");
        validateMagicBytes(file, contentType);
        Path root = root().resolve("products").normalize();
        String fileName = UUID.randomUUID() + extension;
        Path destination = root.resolve(fileName).normalize();
        if (!destination.startsWith(root)) throw new IllegalArgumentException("Invalid storage path");
        try {
            Files.createDirectories(root);
            file.transferTo(destination);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not store image", ex);
        }
        return new StoredFile("/uploads/products/" + fileName, "products/" + fileName, contentType, file.getSize());
    }

    @Override
    public void delete(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) return;
        Path root = root();
        Path target = root.resolve(storagePath).normalize();
        if (!target.startsWith(root)) throw new IllegalArgumentException("Invalid storage path");
        try { Files.deleteIfExists(target); } catch (IOException ignored) { }
    }

    private Path root() { return Path.of(properties.getUploadDir()).toAbsolutePath().normalize(); }

    private void validateMagicBytes(MultipartFile file, String contentType) {
        try (InputStream in = file.getInputStream()) {
            byte[] b = in.readNBytes(12);
            boolean valid = switch (contentType) {
                case "image/jpeg" -> b.length >= 3 && (b[0] & 0xff) == 0xff && (b[1] & 0xff) == 0xd8 && (b[2] & 0xff) == 0xff;
                case "image/png" -> b.length >= 8 && (b[0] & 0xff) == 0x89 && b[1] == 0x50 && b[2] == 0x4e && b[3] == 0x47 && b[4] == 0x0d && b[5] == 0x0a && b[6] == 0x1a && b[7] == 0x0a;
                case "image/webp" -> b.length >= 12 && b[0] == 0x52 && b[1] == 0x49 && b[2] == 0x46 && b[3] == 0x46 && b[8] == 0x57 && b[9] == 0x45 && b[10] == 0x42 && b[11] == 0x50;
                default -> false;
            };
            if (!valid) throw new IllegalArgumentException("Image content does not match declared type");
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not validate image content", ex);
        }
    }
}
