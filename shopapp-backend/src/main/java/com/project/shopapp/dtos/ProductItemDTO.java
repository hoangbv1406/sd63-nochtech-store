package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ProductItemDTO {

    @Min(value = 1, message = "Product ID is required")
    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("variant_id")
    private Long variantId;

    @Min(value = 1, message = "Supplier ID is required")
    @JsonProperty("supplier_id")
    private Long supplierId;

    @NotBlank(message = "IMEI code is required")
    @JsonProperty("imei_code")
    private String imeiCode;

    @JsonProperty("inbound_price")
    private BigDecimal inboundPrice;

}
