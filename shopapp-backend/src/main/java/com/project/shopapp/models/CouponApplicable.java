package com.project.shopapp.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.shopapp.enums.CouponApplicableType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coupon_applicables")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CouponApplicable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    @JsonBackReference
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    @Column(name = "object_type", nullable = false)
    private CouponApplicableType objectType;

    @Column(name = "object_id", nullable = false)
    private Long objectId;

}
