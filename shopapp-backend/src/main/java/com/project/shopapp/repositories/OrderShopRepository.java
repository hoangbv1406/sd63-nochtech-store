package com.project.shopapp.repositories;

import com.project.shopapp.enums.OrderStatus;
import com.project.shopapp.models.OrderShop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderShopRepository extends JpaRepository<OrderShop, Long> {

    List<OrderShop> findByParentOrderId(Long parentOrderId);

    @EntityGraph(attributePaths = {"orderDetails"})
    Page<OrderShop> findByShopId(Long shopId, Pageable pageable);

    @EntityGraph(attributePaths = {"orderDetails"})
    Page<OrderShop> findByShopIdAndStatus(Long shopId, OrderStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"orderDetails"})
    Page<OrderShop> findByShopIdAndCreatedAtBetween(
            Long shopId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

    Optional<OrderShop> findByIdAndShopId(Long id, Long shopId);

    long countByShopIdAndStatus(Long shopId, OrderStatus status);

}
