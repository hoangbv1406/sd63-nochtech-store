package com.project.shopapp.services.orderdetails;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    OrderDetail getOrderDetail(Long id) throws DataNotFoundException;
    OrderDetail createOrderDetail(OrderDetailDTO newOrderDetail) throws Exception;
    OrderDetail updateOrderDetail(Long id, OrderDetailDTO newOrderDetailData) throws Exception;
    void deleteById(Long id);
    List<OrderDetail> findByOrderId(Long orderId);
}
