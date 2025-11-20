package com.project.shopapp.services.orders;

import com.project.shopapp.dtos.CartItemDTO;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.enums.OrderStatus;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.*;
import com.project.shopapp.repositories.*;
import com.project.shopapp.responses.order.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CouponRepository couponRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Override
    @Transactional
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: " + orderDTO.getUserId()));

        modelMapper.typeMap(OrderDTO.class, Order.class).addMappings(mapper -> mapper.skip(Order::setId));
        Order order = new Order();
        modelMapper.map(orderDTO, order);

        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());
        order.setProvinceCode(orderDTO.getProvinceCode());
        order.setDistrictCode(orderDTO.getDistrictCode());
        order.setWardCode(orderDTO.getWardCode());

        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Date must be at least today !");
        }
        order.setShippingDate(shippingDate);

        if (orderDTO.getCartItems() == null || orderDTO.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart items cannot be empty");
        }

        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new DataNotFoundException("Product not found with id: " + productId));

            if (cartItemDTO.getVariantId() != null && cartItemDTO.getVariantId() > 0) {
                ProductVariant variant = productVariantRepository.findById(cartItemDTO.getVariantId())
                        .orElseThrow(() -> new DataNotFoundException("Variant not found"));

                if (variant.getQuantity() < quantity) {
                    throw new RuntimeException("Variant " + variant.getSku() + " out of stock");
                }

                variant.setQuantity(variant.getQuantity() - quantity);
                productVariantRepository.save(variant);

                orderDetail.setPrice(variant.getPrice());
                orderDetail.setVariantName("Variant: " + variant.getId());

            } else {
                orderDetail.setPrice(product.getPrice());
            }

            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);
            orderDetail.setTotalMoney(orderDetail.getPrice().multiply(java.math.BigDecimal.valueOf(quantity)));

            orderDetails.add(orderDetail);
        }
        order.setOrderDetails(orderDetails);

        String couponCode = orderDTO.getCouponCode();
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            Coupon coupon = couponRepository.findByCodeAndActive(couponCode)
                    .orElseThrow(() -> new IllegalArgumentException("Coupon not found or inactive"));
            order.setCoupon(coupon);
        }

        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException {
        Order order = getOrderById(id);
        if (order == null) throw new DataNotFoundException("Order not found");

        if (orderDTO.getFullName() != null) order.setFullName(orderDTO.getFullName());
        if (orderDTO.getEmail() != null) order.setEmail(orderDTO.getEmail());
        if (orderDTO.getPhoneNumber() != null) order.setPhoneNumber(orderDTO.getPhoneNumber());
        if (orderDTO.getAddress() != null) order.setAddress(orderDTO.getAddress());
        if (orderDTO.getStatus() != null) order.setStatus(OrderStatus.valueOf(orderDTO.getStatus()));

        return orderRepository.save(order);
    }

    @Override
    public Order updateOrderStatus(Long id, String status) throws DataNotFoundException {
        Order order = getOrderById(id);
        if (order == null) throw new DataNotFoundException("Order not found");
        order.setStatus(OrderStatus.valueOf(status));
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
    public List<OrderResponse> findByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(OrderResponse::fromOrder).toList();
    }

    @Override
    public Page<Order> getOrdersByKeyword(String keyword, Pageable pageable) {
        return orderRepository.findByKeyword(keyword, pageable);
    }

}
