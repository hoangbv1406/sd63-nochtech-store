package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.enums.WalletTransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransactionDTO {

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "1000.0", inclusive = true, message = "Số tiền giao dịch tối thiểu là 1000đ")
    private BigDecimal amount;

    @NotNull(message = "Loại giao dịch không được để trống")
    @JsonProperty("type")
    private WalletTransactionType type;

    private String description;

}