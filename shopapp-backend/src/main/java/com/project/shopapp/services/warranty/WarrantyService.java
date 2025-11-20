package com.project.shopapp.services.warranty;

import com.project.shopapp.dtos.WarrantyRequestDTO;
import com.project.shopapp.models.WarrantyRequest;

import java.util.List;

public interface WarrantyService {
    List<WarrantyRequest> getAllRequests();
    List<WarrantyRequest> getRequestsByUserId(Long userId);
    WarrantyRequest createWarrantyRequest(WarrantyRequestDTO warrantyRequestDTO) throws Exception;
    WarrantyRequest updateStatus(Long id, String status);
}
