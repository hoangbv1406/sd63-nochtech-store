package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class OrderWithDetailsDTO {

    @Min(value = 1, message = "User ID must be at least 1")
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(min = 5, message = "Phone number must be at least 5 characters")
    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;

    @JsonProperty("province_code")
    private String provinceCode;

    @JsonProperty("district_code")
    private String districtCode;

    @JsonProperty("ward_code")
    private String wardCode;

    @JsonProperty("note")
    private String note;

    @Min(value = 0, message = "Total money must be >= 0")
    @JsonProperty("total_money")
    private BigDecimal totalMoney;

    @Min(value = 0, message = "Shipping fee must be >= 0")
    @JsonProperty("shipping_fee")
    private BigDecimal shippingFee;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("order_details")
    private List<OrderDetailDTO> orderDetailDTOS;

}
