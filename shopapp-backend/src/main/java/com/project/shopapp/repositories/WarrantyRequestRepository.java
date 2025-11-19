package com.project.shopapp.repositories;

import com.project.shopapp.models.WarrantyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarrantyRequestRepository extends JpaRepository<WarrantyRequest, Long> {
    List<WarrantyRequest> findByUserId(Long userId);
    List<WarrantyRequest> findByOrderDetailId(Long orderDetailId);
}
