package com.smart.ecommerce.dashboard.repository;
import com.smart.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DashboardCategoryRepository
    extends JpaRepository<Category, Long> {}
