package com.smart.ecommerce.repository;
import com.smart.ecommerce.entity.SocialAccount;import com.smart.ecommerce.enums.SocialProvider;import java.util.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface SocialAccountRepository extends JpaRepository<SocialAccount,Long>{ Optional<SocialAccount> findByProviderAndProviderUserId(SocialProvider p,String id); }
