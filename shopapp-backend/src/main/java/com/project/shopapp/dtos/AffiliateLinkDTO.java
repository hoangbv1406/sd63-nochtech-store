package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AffiliateLinkDTO {

    @JsonProperty("product_id")
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Size(min = 5, max = 50, message = "Mã Affiliate phải từ 5 đến 50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Mã Affiliate chỉ được chứa chữ cái, chữ số và dấu gạch dưới (Không chứa dấu cách hay ký tự đặc biệt)")
    private String code;

}
