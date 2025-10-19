package com.project.shopapp.repositories;

import com.project.shopapp.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByVnpTxnRef(String vnpTxnRef);
    List<Order> findByUserId(Long userId);

    @Query("SELECT o FROM Order o WHERE o.active = true AND (COALESCE(TRIM(:keyword),'') = '' OR LOWER(o.fullName) LIKE CONCAT('%', LOWER(TRIM(:keyword)), '%') OR LOWER(o.address) LIKE CONCAT('%', LOWER(TRIM(:keyword)), '%') OR LOWER(o.note) LIKE CONCAT('%', LOWER(TRIM(:keyword)), '%') OR LOWER(o.email) LIKE CONCAT('%', LOWER(TRIM(:keyword)), '%')) ORDER BY o.orderDate DESC")
    Page<Order> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

}
