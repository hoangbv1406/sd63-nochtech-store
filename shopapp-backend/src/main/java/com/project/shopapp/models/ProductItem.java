package com.project.shopapp.models;

import com.project.shopapp.enums.ProductItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_items")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ProductItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "imei_code", nullable = false, unique = true, length = 50)
    private String imeiCode;

    @Column(name = "inbound_price")
    private BigDecimal inboundPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private ProductItemStatus status = ProductItemStatus.AVAILABLE;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "import_date")
    @Builder.Default
    private LocalDateTime importDate = LocalDateTime.now();

    @Column(name = "sold_date")
    private LocalDateTime soldDate;

}
