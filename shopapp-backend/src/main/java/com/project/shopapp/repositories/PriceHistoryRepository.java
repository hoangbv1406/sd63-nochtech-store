package com.project.shopapp.repositories;

import com.project.shopapp.models.PriceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    List<PriceHistory> findByProductIdOrderByCreatedAtDesc(Long productId);

    List<PriceHistory> findTop10ByProductIdOrderByCreatedAtDesc(Long productId);

    Page<PriceHistory> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);

    List<PriceHistory> findTop10ByVariantIdOrderByCreatedAtDesc(Long variantId);

}
