package com.project.shopapp.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_details")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_item_id")
    private ProductItem productItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "cost_price")
    private BigDecimal costPrice;

    @Column(name = "total_money", nullable = false)
    private BigDecimal totalMoney;

    @Column(name = "number_of_products", nullable = false)
    private int numberOfProducts;

    @Column(name = "configuration", columnDefinition = "json")
    private String configuration;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "variant_name")
    private String variantName;

    @Column(name = "warranty_expire_date")
    private LocalDate warrantyExpireDate;

    @Column(name = "is_settled")
    private boolean isSettled;

    @Column(name = "settlement_date")
    private LocalDateTime settlementDate;

    @Column(name = "settlement_note")
    private String settlementNote;

}
