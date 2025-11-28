package com.project.shopapp.repositories;

import com.project.shopapp.enums.ProductItemStatus;
import com.project.shopapp.models.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {

    Optional<ProductItem> findByImeiCode(String imeiCode);

    boolean existsByImeiCode(String imeiCode);

    @Query("SELECT pi FROM ProductItem pi WHERE pi.variant.id = :variantId AND pi.status = :status")
    List<ProductItem> findAvailableItemsByVariant(
            @Param("variantId") Long variantId,
            @Param("status") ProductItemStatus status
    );

    List<ProductItem> findByStatusAndLockedUntilBefore(ProductItemStatus hold, LocalDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ProductItem pi SET pi.status = :available, pi.lockedUntil = NULL WHERE pi.status = :hold AND pi.lockedUntil < :now")
    int unlockExpiredHoldItems(
            @Param("hold") ProductItemStatus hold,
            @Param("available") ProductItemStatus available,
            @Param("now") LocalDateTime now
    );

}
