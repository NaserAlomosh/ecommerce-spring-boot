package com.smart.ecommerce.repository;
import com.smart.ecommerce.entity.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
  List<OrderItem> findByOrderId(Long orderId);
}
