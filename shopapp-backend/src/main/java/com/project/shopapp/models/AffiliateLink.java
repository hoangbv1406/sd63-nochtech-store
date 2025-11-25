package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "affiliate_links")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AffiliateLink extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false, unique = true)
    private String code;

    private Integer clicks;

}
