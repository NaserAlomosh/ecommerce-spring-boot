package com.smart.ecommerce.repository;
import com.smart.ecommerce.entity.*;
import com.smart.ecommerce.enums.OtpPurpose;
import java.time.Instant;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {
  Optional<EmailOtp>
  findFirstByUserAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(
      User u, OtpPurpose p);
  @Modifying @Query("update EmailOtp e set e.consumedAt=:now where e.user=:user and e.purpose=:purpose and e.consumedAt is null") int invalidate(@Param("user")User u,@Param("purpose")OtpPurpose p,@Param("now")Instant now);
}
