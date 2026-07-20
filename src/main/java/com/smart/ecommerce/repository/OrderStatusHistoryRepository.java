package com.smart.ecommerce.repository;
import com.smart.ecommerce.entity.OrderStatusHistory;import java.util.List;import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> { List<OrderStatusHistory> findByOrderIdOrderByChangedAtAsc(Long orderId); }
