package com.project.shopapp.services.warranty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shopapp.dtos.WarrantyRequestDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.models.User;
import com.project.shopapp.models.WarrantyRequest;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.repositories.WarrantyRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarrantyServiceImpl implements WarrantyService {
    private final WarrantyRequestRepository warrantyRequestRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;

    @Override
    public List<WarrantyRequest> getAllRequests() {
        return warrantyRequestRepository.findAll();
    }

    @Override
    public List<WarrantyRequest> getRequestsByUserId(Long userId) {
        return warrantyRequestRepository.findByUserId(userId);
    }

    @Override
    public WarrantyRequest createWarrantyRequest(WarrantyRequestDTO dto) throws Exception {
        OrderDetail orderDetail = orderDetailRepository.findById(dto.getOrderDetailId())
                .orElseThrow(() -> new DataNotFoundException("Order detail not found"));

        User user = orderDetail.getOrder().getUser();

        WarrantyRequest request = WarrantyRequest.builder()
                .user(user)
                .orderDetail(orderDetail)
                .requestType(dto.getRequestType())
                .reason(dto.getReason())
                .quantity(dto.getQuantity())
                .status("PENDING")
                .build();

        if (dto.getImages() != null) {
            ObjectMapper mapper = new ObjectMapper();
            request.setImages(mapper.writeValueAsString(dto.getImages()));
        }

        return warrantyRequestRepository.save(request);
    }

    @Override
    public WarrantyRequest updateStatus(Long id, String status) {
        WarrantyRequest request = warrantyRequestRepository.findById(id).orElseThrow();
        request.setStatus(status);
        return warrantyRequestRepository.save(request);
    }

}
