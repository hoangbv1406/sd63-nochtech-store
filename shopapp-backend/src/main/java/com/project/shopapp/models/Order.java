package com.project.shopapp.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.shopapp.enums.OrderChannel;
import com.project.shopapp.enums.OrderStatus;
import com.project.shopapp.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "fullname", length = 100)
    private String fullName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 100)
    private String phoneNumber;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "province_code")
    private String provinceCode;

    @Column(name = "district_code")
    private String districtCode;

    @Column(name = "ward_code")
    private String wardCode;

    @Column(name = "note", length = 100)
    private String note;

    @Column(name = "order_date")
    @Builder.Default
    private LocalDateTime orderDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "sub_total")
    private BigDecimal subTotal;

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "total_money")
    private BigDecimal totalMoney;

    @Column(name = "total_cost_price")
    private BigDecimal totalCostPrice;

    @Column(name = "shipping_method")
    private String shippingMethod;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "shipping_date")
    private LocalDate shippingDate;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_channel")
    @Builder.Default
    private OrderChannel orderChannel = OrderChannel.ONLINE;

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pos_session_id")
    private PosSession posSession;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @OneToMany(mappedBy = "parentOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private List<OrderShop> subOrders = new ArrayList<>();

}
