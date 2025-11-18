package com.project.shopapp.repositories;

import com.project.shopapp.models.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
    Optional<ProductItem> findByImeiCode(String imeiCode);
    boolean existsByImeiCode(String imeiCode);

    @Query("SELECT pi FROM ProductItem pi WHERE pi.variant.id = :variantId AND pi.status = 'AVAILABLE'")
    List<ProductItem> findAvailableItemsByVariant(Long variantId);
}