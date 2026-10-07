package com.smart.ecommerce.repository;
import com.smart.ecommerce.entity.RefreshToken;
import com.smart.ecommerce.entity.User;
import java.time.Instant;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface RefreshTokenRepository
    extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByTokenHash(String tokenHash);
  @Modifying @Query("update RefreshToken r set r.revokedAt=:now where r.user=:user and r.revokedAt is null") int revokeAll(@Param("user") User user,@Param("now") Instant now);
}
