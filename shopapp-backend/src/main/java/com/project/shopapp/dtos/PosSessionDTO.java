package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PosSessionDTO {

    @JsonProperty("shop_id")
    @NotNull(message = "ID Cửa hàng không được để trống")
    private Long shopId;

    @JsonProperty("opening_cash")
    @DecimalMin(value = "0.0", inclusive = true, message = "Tiền mặt đầu ca không được là số âm")
    private BigDecimal openingCash;

    private String note;

}
