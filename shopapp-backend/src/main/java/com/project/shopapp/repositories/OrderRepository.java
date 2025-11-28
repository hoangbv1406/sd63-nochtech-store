package com.project.shopapp.repositories;

import com.project.shopapp.enums.OrderStatus;
import com.project.shopapp.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    Optional<Order> findByVnpTxnRef(String vnpTxnRef);

    @Query("SELECT o FROM Order o WHERE o.active = true AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(o.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "o.phoneNumber LIKE CONCAT('%', :keyword, '%') OR " +
            "LOWER(o.trackingNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR o.status = :status)")
    Page<Order> findByKeywordAndStatus(@Param("keyword") String keyword,
                                       @Param("status") OrderStatus status,
                                       Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.orderDate < :cutoffDate")
    List<Order> findExpiredPendingOrders(@Param("status") OrderStatus status,
                                         @Param("cutoffDate") LocalDateTime cutoffDate);

}
