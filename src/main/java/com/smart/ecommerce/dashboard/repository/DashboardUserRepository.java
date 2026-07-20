package com.smart.ecommerce.dashboard.repository;
import com.smart.ecommerce.entity.User;import com.smart.ecommerce.enums.Role;import org.springframework.data.jpa.repository.JpaRepository;
public interface DashboardUserRepository extends JpaRepository<User, Long> { long countByRole(Role role); }
