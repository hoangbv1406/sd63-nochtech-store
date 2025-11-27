package com.project.shopapp.repositories;

import com.project.shopapp.models.AffiliateLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AffiliateLinkRepository extends JpaRepository<AffiliateLink, Long> {

    @Query("SELECT a FROM AffiliateLink a JOIN FETCH a.user JOIN FETCH a.product WHERE a.code = :code")
    Optional<AffiliateLink> findByCode(@Param("code") String code);

    boolean existsByCode(String code);

    Optional<AffiliateLink> findByUserIdAndProductId(Long userId, Long productId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE AffiliateLink a SET a.clicks = a.clicks + 1 WHERE a.code = :code")
    void incrementClicksByCode(@Param("code") String code);

}
