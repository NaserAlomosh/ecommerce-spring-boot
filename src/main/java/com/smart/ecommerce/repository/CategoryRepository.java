package com.smart.ecommerce.repository;

import com.smart.ecommerce.entity.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  List<Category> findByActiveTrue(Sort sort);

  Optional<Category> findByNameEnIgnoreCase(String nameEn);

  Optional<Category> findByNameArIgnoreCase(String nameAr);

  boolean existsByNameEnIgnoreCaseAndIdNot(String nameEn, Long id);

  boolean existsByNameArIgnoreCaseAndIdNot(String nameAr, Long id);
}
