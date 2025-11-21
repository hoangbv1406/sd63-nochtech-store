package com.project.shopapp.controllers;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.order.OrderDetailResponse;
import com.project.shopapp.services.orderdetails.OrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/order-details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;

    @GetMapping("/{orderDetailId}")
    public ResponseEntity<?> getOrderDetail(@Valid @PathVariable("orderDetailId") Long orderDetailId) throws DataNotFoundException {
        OrderDetail orderDetail = orderDetailService.getOrderDetail(orderDetailId);
        OrderDetailResponse orderDetailResponse = OrderDetailResponse.fromOrderDetail(orderDetail);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Order detail retrieved successfully. orderDetailId = " + orderDetailId)
                .status(HttpStatus.OK)
                .data(orderDetailResponse)
                .build()
        );
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject> createOrderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO) throws Exception {
        OrderDetail newOrderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
        OrderDetailResponse orderDetailResponse = OrderDetailResponse.fromOrderDetail(newOrderDetail);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Order detail created successfully.")
                .status(HttpStatus.CREATED)
                .data(orderDetailResponse)
                .build()
        );
    }

    @PutMapping("/{orderDetailId}")
    public ResponseEntity<ResponseObject> updateOrderDetail(
            @Valid @PathVariable("orderDetailId") Long orderDetailId,
            @RequestBody OrderDetailDTO orderDetailDTO
    ) throws Exception {
        OrderDetail orderDetail = orderDetailService.updateOrderDetail(orderDetailId, orderDetailDTO);
        // Trả về DTO thay vì Entity
        return ResponseEntity.ok().body(ResponseObject.builder()
                .data(OrderDetailResponse.fromOrderDetail(orderDetail))
                .message("Order detail updated successfully. orderDetailId = " + orderDetailId)
                .status(HttpStatus.OK)
                .build()
        );
    }

    @DeleteMapping("/{orderDetailId}")
    public ResponseEntity<ResponseObject> deleteOrderDetail(@Valid @PathVariable("orderDetailId") Long orderDetailId) {
        orderDetailService.deleteById(orderDetailId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Order detail deleted successfully. orderDetailId = " + orderDetailId)
                .status(HttpStatus.OK)
                .build()
        );
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ResponseObject> getOrderDetails(@Valid @PathVariable("orderId") Long orderId) {
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponses = orderDetails.stream()
                .map(OrderDetailResponse::fromOrderDetail)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Order details retrieved successfully. orderId = " + orderId)
                .status(HttpStatus.OK)
                .data(orderDetailResponses)
                .build()
        );
    }

}
