package com.smart.ecommerce.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PaginationResponse<T>(List<T> content, int page, int size,
                                    long totalElements, int totalPages,
                                    boolean first, boolean last) {
  public static <T> PaginationResponse<T> from(Page<T> page) {
    return new PaginationResponse<>(page.getContent(), page.getNumber(),
                                    page.getSize(), page.getTotalElements(),
                                    page.getTotalPages(), page.isFirst(),
                                    page.isLast());
  }
}
