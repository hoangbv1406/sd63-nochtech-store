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
public class Order {

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

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "address", nullable = false, length = 200)
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
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

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
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_channel")
    private OrderChannel orderChannel;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "vnp_txn_ref")
    private String vnpTxnRef;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
        if (active == null) active = true;
        if (status == null) status = OrderStatus.PENDING;
        if (paymentStatus == null) paymentStatus = PaymentStatus.UNPAID;
        if (orderChannel == null) orderChannel = OrderChannel.ONLINE;
    }

}
