package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.enums.OrderChannel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(min = 5, message = "Số điện thoại phải có ít nhất 5 ký tự")
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

    @DecimalMin(value = "0.0", inclusive = true, message = "Tổng tiền không được là số âm")
    @JsonProperty("total_money")
    private BigDecimal totalMoney;

    @DecimalMin(value = "0.0", inclusive = true, message = "Phí ship không được là số âm")
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

    @JsonProperty("coupon_code")
    private String couponCode;

    @JsonProperty("vnp_txn_ref")
    private String vnpTxnRef;

    @NotEmpty(message = "Giỏ hàng không được để trống")
    @JsonProperty("cart_items")
    private List<CartItemDTO> cartItems;

    @JsonProperty("pos_session_id")
    private Long posSessionId;

    @JsonProperty("order_channel")
    private OrderChannel orderChannel;

}
