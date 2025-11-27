package com.project.shopapp.repositories;

import com.project.shopapp.enums.ShopStatus; // Đảm bảo cậu có Enum này nhé
import com.project.shopapp.models.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    boolean existsBySlug(String slug);

    Optional<Shop> findByOwnerId(Long ownerId);

    Optional<Shop> findBySlug(String slug);

    Optional<Shop> findBySlugAndStatus(String slug, ShopStatus status);

    Page<Shop> findByStatus(ShopStatus status, Pageable pageable);

    Page<Shop> findByNameContainingIgnoreCaseAndStatus(String name, ShopStatus status, Pageable pageable);

}
