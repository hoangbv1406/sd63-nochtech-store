package com.project.shopapp.models;

import com.project.shopapp.enums.WalletTransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "wallet_transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private WalletTransactionType type;

    private String description;

    @Column(name = "ref_order_id")
    private Long refOrderId;

}
