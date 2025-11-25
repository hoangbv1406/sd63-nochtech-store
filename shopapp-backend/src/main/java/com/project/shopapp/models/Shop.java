package com.project.shopapp.models;

import com.project.shopapp.enums.ShopStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "shops")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Shop extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String address;

    @Column(name = "commission_rate")
    @Builder.Default
    private BigDecimal commissionRate = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ShopStatus status = ShopStatus.PENDING;

    @Column(name = "rating_avg")
    @Builder.Default
    private Float ratingAvg = 5.0f;

    @Column(name = "total_orders")
    @Builder.Default
    private Integer totalOrders = 0;

}
