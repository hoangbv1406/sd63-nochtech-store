package com.project.shopapp.responses.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletResponse {

    @JsonProperty("user_id")
    private Long userId;

    private BigDecimal balance;

    @JsonProperty("frozen_balance")
    private BigDecimal frozenBalance;

}
