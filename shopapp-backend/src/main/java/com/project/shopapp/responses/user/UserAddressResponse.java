package com.project.shopapp.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.UserAddress;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserAddressResponse {

    private Long id;

    @JsonProperty("recipient_name")
    private String recipientName;

    @JsonProperty("phone_number")
    private String phoneNumber;

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

    public static UserAddressResponse fromUserAddress(UserAddress address) {
        return UserAddressResponse.builder()
                .id(address.getId())
                .recipientName(address.getRecipientName())
                .phoneNumber(address.getPhoneNumber())
                .addressDetail(address.getAddressDetail())
                .provinceCode(address.getProvinceCode())
                .districtCode(address.getDistrictCode())
                .wardCode(address.getWardCode())
                .isDefault(address.isDefault())
                .build();
    }

}
