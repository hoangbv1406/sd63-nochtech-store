package com.project.shopapp.repositories;

import com.project.shopapp.enums.PosSessionStatus;
import com.project.shopapp.models.PosSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PosSessionRepository extends JpaRepository<PosSession, Long> {

    Optional<PosSession> findByUserIdAndStatus(Long userId, PosSessionStatus status);

    Optional<PosSession> findByShopIdAndUserIdAndStatus(Long shopId, Long userId, PosSessionStatus status);

    Page<PosSession> findByShopIdOrderByCreatedAtDesc(Long shopId, Pageable pageable);

}
