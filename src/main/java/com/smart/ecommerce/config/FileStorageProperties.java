package com.smart.ecommerce.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.file-storage")
public class FileStorageProperties {
    @NotBlank
    private String uploadDir = "uploads";

    @DataSizeUnit(DataUnit.MEGABYTES)
    private DataSize maxImageSize = DataSize.ofMegabytes(5);

    @Min(0)
    private int maxImagesPerProduct = 10;
}
