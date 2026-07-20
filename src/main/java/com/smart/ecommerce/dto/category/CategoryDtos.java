package com.smart.ecommerce.dto.category;

import jakarta.validation.constraints.NotBlank;

public final class CategoryDtos {
    private CategoryDtos() {}

    public record CategoryRequest(
            @NotBlank String nameEn,
            @NotBlank String nameAr,
            boolean active) {}

    public record CategoryResponse(Long id, String nameEn, String nameAr, boolean active) {}

    public record CategorySummary(Long id, String nameEn, String nameAr) {}
}
