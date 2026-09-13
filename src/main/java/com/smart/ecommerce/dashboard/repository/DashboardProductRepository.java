package com.smart.ecommerce.dashboard.repository;

import com.smart.ecommerce.entity.Product;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DashboardProductRepository
    extends JpaRepository<Product, Long> {
  @Query("select count(p) as totalProducts, coalesce(sum(case when p.active " +
         "= true then 1 else 0 end), 0) as activeProducts, coalesce(sum(case " +
         "when p.active = false then 1 else 0 end), 0) as inactiveProducts " +
         "from Product p where p.deleted = false")
  ProductCountsProjection
  countProducts();
  @Query("select count(p) from Product p where p.deleted = false and " +
         "p.stockQuantity > 0 and p.stockQuantity <= :threshold")
  long
  countLowStock(@Param("threshold") int threshold);
  @Query("select count(p) from Product p where p.deleted = false and " +
         "p.stockQuantity <= 0")
  long
  countOutOfStock();
  @Query(
      "select p.id as productId, p.nameEn as name, p.stockQuantity as stock " +
      "from Product p where p.deleted = false and p.stockQuantity > 0 and " +
      "p.stockQuantity <= :threshold order by p.stockQuantity asc, p.id asc")
  List<LowStockProductProjection>
  findLowStock(@Param("threshold") int threshold, Pageable pageable);
  @Query("select p.id as productId, p.nameEn as name from Product p where " +
         "p.deleted = false and p.stockQuantity <= 0 order by p.id asc")
  List<OutOfStockProductProjection>
  findOutOfStock(Pageable pageable);
  interface ProductCountsProjection {
    long getTotalProducts();
    long getActiveProducts();
    long getInactiveProducts();
  }
  interface LowStockProductProjection {
    Long getProductId();
    String getName();
    int getStock();
  }
  interface OutOfStockProductProjection {
    Long getProductId();
    String getName();
  }
}
