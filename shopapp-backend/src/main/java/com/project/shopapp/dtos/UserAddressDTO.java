package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class UserAddressDTO {

    @JsonProperty("user_id")
    private Long userId;

    @NotBlank(message = "Recipient name is required")
    @JsonProperty("recipient_name")
    private String recipientName;

    @NotBlank(message = "Phone number is required")
    @JsonProperty("phone_number")
    private String phoneNumber;

    @NotBlank(message = "Address detail is required")
    @JsonProperty("address_detail")
    private String addressDetail;

    @JsonProperty("province_code")
    private String provinceCode;

    @JsonProperty("district_code")
    private String districtCode;

    @JsonProperty("ward_code")
    private String wardCode;

    @JsonProperty("is_default")
    private boolean isDefault;

}
