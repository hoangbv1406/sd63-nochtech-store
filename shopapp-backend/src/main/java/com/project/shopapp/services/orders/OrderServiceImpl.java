package com.project.shopapp.services.orders;

import com.project.shopapp.dtos.CartItemDTO;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.enums.OrderStatus;
import com.project.shopapp.shared.exceptions.DataNotFoundException;
import com.project.shopapp.shared.exceptions.InvalidParamException;
import com.project.shopapp.models.*;
import com.project.shopapp.repositories.*;
import com.project.shopapp.responses.order.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CouponRepository couponRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderShopRepository orderShopRepository;
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
    public Order createOrder(OrderDTO orderDTO, Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng với id: " + userId));

        Order order = new Order();
        modelMapper.map(orderDTO, order);

        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderChannel(orderDTO.getOrderChannel() != null ? orderDTO.getOrderChannel() : com.project.shopapp.enums.OrderChannel.ONLINE);
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());
        order.setProvinceCode(orderDTO.getProvinceCode());
        order.setDistrictCode(orderDTO.getDistrictCode());
        order.setWardCode(orderDTO.getWardCode());

        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now().plusDays(3) : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new InvalidParamException("Ngày giao hàng phải từ hôm nay trở đi!");
        }
        order.setShippingDate(shippingDate);

        if (orderDTO.getCartItems() == null || orderDTO.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng không được để trống");
        }

        if (orderDTO.getCouponCode() != null && !orderDTO.getCouponCode().trim().isEmpty()) {
            Coupon coupon = couponRepository.findByCodeAndActive(orderDTO.getCouponCode())
                    .orElseThrow(() -> new IllegalArgumentException("Mã giảm giá không tồn tại hoặc đã hết hạn"));
            order.setCoupon(coupon);
        }

        order = orderRepository.save(order);

        Map<Long, List<OrderDetail>> shopOrderDetailsMap = new HashMap<>();
        double grandTotal = 0;

        for (CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new DataNotFoundException("Không tìm thấy sản phẩm với id: " + productId));

            if (cartItemDTO.getVariantId() != null && cartItemDTO.getVariantId() > 0) {
                ProductVariant variant = productVariantRepository.findById(cartItemDTO.getVariantId())
                        .orElseThrow(() -> new DataNotFoundException("Không tìm thấy biến thể sản phẩm"));

                if (variant.getQuantity() < quantity) {
                    throw new RuntimeException("Biến thể " + variant.getSku() + " đã hết hàng trong kho!");
                }
                variant.setQuantity(variant.getQuantity() - quantity);
                productVariantRepository.save(variant);

                orderDetail.setPrice(variant.getPrice());
                orderDetail.setVariantName("Variant ID: " + variant.getId());
            } else {
                orderDetail.setPrice(product.getPrice());
            }

            orderDetail.setProduct(product);
            orderDetail.setProductName(product.getName());
            orderDetail.setNumberOfProducts(quantity);
            orderDetail.setTotalMoney(orderDetail.getPrice().multiply(BigDecimal.valueOf(quantity)));

            grandTotal += orderDetail.getTotalMoney().doubleValue();

            Long shopId = product.getShop() != null ? product.getShop().getId() : 1L;
            shopOrderDetailsMap.computeIfAbsent(shopId, k -> new ArrayList<>()).add(orderDetail);
        }

        for (Map.Entry<Long, List<OrderDetail>> entry : shopOrderDetailsMap.entrySet()) {
            Long shopId = entry.getKey();
            List<OrderDetail> details = entry.getValue();

            BigDecimal subTotal = details.stream()
                    .map(OrderDetail::getTotalMoney)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal adminCommission = subTotal.multiply(BigDecimal.valueOf(0.05));

            OrderShop orderShop = OrderShop.builder()
                    .parentOrder(order)
                    .shop(Shop.builder().id(shopId).build())
                    .subTotal(subTotal)
                    .status(OrderStatus.PENDING)
                    .shippingFee(BigDecimal.ZERO)
                    .adminCommission(adminCommission)
                    .shopIncome(subTotal.subtract(adminCommission))
                    .build();

            OrderShop savedOrderShop = orderShopRepository.save(orderShop);

            for (OrderDetail detail : details) {
                detail.setOrderShop(savedOrderShop);
                orderDetailRepository.save(detail);
            }
        }

        order.setTotalMoney(BigDecimal.valueOf(grandTotal));
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException {
        Order order = getOrderById(id);
        if (order == null) throw new DataNotFoundException("Không tìm thấy đơn hàng");

        if (orderDTO.getFullName() != null) order.setFullName(orderDTO.getFullName());
        if (orderDTO.getEmail() != null) order.setEmail(orderDTO.getEmail());
        if (orderDTO.getPhoneNumber() != null) order.setPhoneNumber(orderDTO.getPhoneNumber());
        if (orderDTO.getAddress() != null) order.setAddress(orderDTO.getAddress());

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long id, String status) throws DataNotFoundException {
        Order order = getOrderById(id);
        if (order == null) throw new DataNotFoundException("Không tìm thấy đơn hàng");

        order.setStatus(OrderStatus.fromString(status));
        return orderRepository.save(order);
    }

    @Override
    @Transactional
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
    public Page<Order> getOrdersByKeyword(String keyword, String status, Pageable pageable) {
        OrderStatus orderStatus = (status != null && !status.trim().isEmpty())
                ? OrderStatus.fromString(status)
                : null;

        return orderRepository.findByKeywordAndStatus(keyword, orderStatus, pageable);
    }

    @Override
    public Page<OrderShop> getOrdersByShopId(Long shopId, Pageable pageable) {
        return orderShopRepository.findByShopId(shopId, pageable);
    }
}
