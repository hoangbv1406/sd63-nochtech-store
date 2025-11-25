package com.project.shopapp.models;

import com.project.shopapp.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orders_shop")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderShop extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_order_id", nullable = false)
    private Order parentOrder;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(name = "shipping_method")
    private String shippingMethod;

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee;

    @Column(name = "sub_total")
    private BigDecimal subTotal;

    @Column(name = "admin_commission")
    private BigDecimal adminCommission;

    @Column(name = "shop_income")
    private BigDecimal shopIncome;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

}
