package com.project.shopapp.services.orders;

import com.project.shopapp.dtos.CartItemDTO;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.*;
import com.project.shopapp.repositories.*;
import com.project.shopapp.responses.order.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            order = orderRepository.findByVnpTxnRef(orderId.toString()).orElse(null);
        }
        return order;
    }

    @Override
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        if (orderDTO == null) {
            throw new IllegalArgumentException("OrderDTO is required");
        }
        if (orderDTO.getUserId() == null) {
            throw new DataNotFoundException("UserId is required in orderDTO");
        }
        User user = userRepository.findById(orderDTO.getUserId()).orElseThrow(() -> new DataNotFoundException("Cannot find user with id: " + orderDTO.getUserId()));
        modelMapper.typeMap(OrderDTO.class, Order.class).addMappings(mapper -> mapper.skip(Order::setId));
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Date must be at least today !");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());

        if (orderDTO.getVnpTxnRef() != null) {
            order.setVnpTxnRef(orderDTO.getVnpTxnRef());
        }

        if (orderDTO.getShippingAddress() == null) {
            order.setShippingAddress(orderDTO.getAddress());
        }

        if (orderDTO.getCartItems() == null || orderDTO.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart items cannot be empty");
        }

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();
            Product product = productRepository.findById(productId).orElseThrow(() -> new DataNotFoundException("Product not found with id: " + productId));
            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);
            orderDetail.setPrice(product.getPrice());
            orderDetail.setOrder(order);
            order.getOrderDetails().add(orderDetail);
            orderDetails.add(orderDetail);
        }

        String couponCode = orderDTO.getCouponCode();
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            Coupon coupon = couponRepository.findByCode(couponCode).orElseThrow(() -> new IllegalArgumentException("Coupon not found"));
            if (!coupon.isActive()) {
                throw new IllegalArgumentException("Coupon is not active");
            }
            order.setCoupon(coupon);
        } else {
            order.setCoupon(null);
        }
        Order savedOrder = orderRepository.save(order);
        return savedOrder;
    }

    @Override
    public Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException {
        Order order = getOrderById(id);
        User existingUser = userRepository.findById(orderDTO.getUserId()).orElseThrow(() -> new DataNotFoundException("Cannot find user with id: " + id));
        if (orderDTO.getUserId() != null) {
            User user = new User();
            user.setId(orderDTO.getUserId());
            order.setUser(user);
        }
        if (orderDTO.getFullName() != null && !orderDTO.getFullName().trim().isEmpty()) {
            order.setFullName(orderDTO.getFullName().trim());
        }
        if (orderDTO.getEmail() != null && !orderDTO.getEmail().trim().isEmpty()) {
            order.setEmail(orderDTO.getEmail().trim());
        }
        if (orderDTO.getPhoneNumber() != null && !orderDTO.getPhoneNumber().trim().isEmpty()) {
            order.setPhoneNumber(orderDTO.getPhoneNumber().trim());
        }
        if (orderDTO.getStatus() != null && !orderDTO.getStatus().trim().isEmpty()) {
            order.setStatus(orderDTO.getStatus().trim());
        }
        if (orderDTO.getAddress() != null && !orderDTO.getAddress().trim().isEmpty()) {
            order.setAddress(orderDTO.getAddress().trim());
        }
        if (orderDTO.getNote() != null && !orderDTO.getNote().trim().isEmpty()) {
            order.setNote(orderDTO.getNote().trim());
        }
        if (orderDTO.getTotalMoney() != null) {
            order.setTotalMoney(orderDTO.getTotalMoney());
        }
        if (orderDTO.getShippingMethod() != null && !orderDTO.getShippingMethod().trim().isEmpty()) {
            order.setShippingMethod(orderDTO.getShippingMethod().trim());
        }
        if (orderDTO.getShippingAddress() != null && !orderDTO.getShippingAddress().trim().isEmpty()) {
            order.setShippingAddress(orderDTO.getShippingAddress().trim());
        }
        if (orderDTO.getShippingDate() != null) {
            order.setShippingDate(orderDTO.getShippingDate());
        }
        if (orderDTO.getPaymentMethod() != null && !orderDTO.getPaymentMethod().trim().isEmpty()) {
            order.setPaymentMethod(orderDTO.getPaymentMethod().trim());
        }

        order.setUser(existingUser);
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order != null) {
            order.setActive(false);
            orderRepository.save(order);
        }
    }

    @Override
    public Order updateOrderStatus(Long id, String status) throws IllegalArgumentException {
        Order order = getOrderById(id);

        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        if (!OrderStatus.VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        String currentStatus = order.getStatus();
        if (currentStatus.equals(OrderStatus.DELIVERED) && !status.equals(OrderStatus.CANCELLED)) {
            throw new IllegalArgumentException("Cannot change status from DELIVERED to " + status);
        }
        if (currentStatus.equals(OrderStatus.CANCELLED)) {
            throw new IllegalArgumentException("Cannot change status of a CANCELLED order");
        }
        if (status.equals(OrderStatus.CANCELLED)) {
            if (!currentStatus.equals(OrderStatus.PENDING)) {
                throw new IllegalArgumentException("Order can only be cancelled from PENDING status");
            }
        }

        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public List<OrderResponse> findByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(order -> OrderResponse.fromOrder(order)).toList();
    }

    @Override
    public Page<Order> getOrdersByKeyword(String keyword, Pageable pageable) {
        return orderRepository.findByKeyword(keyword, pageable);
    }

}
