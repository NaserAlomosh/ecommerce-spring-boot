package com.smart.ecommerce.repository;
import com.smart.ecommerce.entity.PasswordResetToken;import java.util.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long>{ Optional<PasswordResetToken> findByTokenHash(String tokenHash); }
