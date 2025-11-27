package com.project.shopapp.responses.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.WalletTransaction;
import com.project.shopapp.responses.BaseResponse;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletTransactionResponse extends BaseResponse {

    private Long id;

    @JsonProperty("wallet_id")
    private Long walletId;

    private BigDecimal amount;

    private String type;

    private String description;

    @JsonProperty("order_id")
    private Long orderId;

    public static WalletTransactionResponse fromTransaction(WalletTransaction transaction) {
        WalletTransactionResponse response = WalletTransactionResponse.builder()
                .id(transaction.getId())
                .walletId(transaction.getWallet() != null ? transaction.getWallet().getId() : null)
                .amount(transaction.getAmount())
                .type(transaction.getType() != null ? transaction.getType().name() : null)
                .description(transaction.getDescription())
                .orderId(transaction.getRefOrderId())
                .build();

        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());
        return response;
    }

}
