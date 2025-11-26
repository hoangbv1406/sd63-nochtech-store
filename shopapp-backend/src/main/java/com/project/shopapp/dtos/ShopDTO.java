package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopDTO {

    @NotBlank(message = "Tên cửa hàng không được để trống")
    @Size(min = 3, max = 200, message = "Tên cửa hàng phải từ 3 đến 200 ký tự")
    private String name;

    @Size(max = 2000, message = "Mô tả cửa hàng không được vượt quá 2000 ký tự")
    private String description;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("banner_url")
    private String bannerUrl;

    @NotBlank(message = "Địa chỉ lấy hàng không được để trống")
    @Size(max = 255, message = "Địa chỉ quá dài")
    private String address;

}
