package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class WarrantyRequestDTO {

    @Min(value = 1, message = "Order Detail ID is required")
    @JsonProperty("order_detail_id")
    private Long orderDetailId;

    @JsonProperty("product_item_id")
    private Long productItemId;

    @NotBlank(message = "Request type is required")
    @JsonProperty("request_type")
    private String requestType;

    @NotBlank(message = "Reason is required")
    @JsonProperty("reason")
    private String reason;

    @Min(value = 1, message = "Quantity must be at least 1")
    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("images")
    private List<String> images;

}
