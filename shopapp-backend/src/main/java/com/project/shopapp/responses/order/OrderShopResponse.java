package com.project.shopapp.responses.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.OrderShop;
import com.project.shopapp.shared.base.BaseResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderShopResponse extends BaseResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("parent_order_id")
    private Long parentOrderId;

    @JsonProperty("shop_id")
    private Long shopId;

    @JsonProperty("shop_name")
    private String shopName;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_fee")
    private BigDecimal shippingFee;

    @JsonProperty("sub_total")
    private BigDecimal subTotal;

    @JsonProperty("admin_commission")
    private BigDecimal adminCommission;

    @JsonProperty("shop_income")
    private BigDecimal shopIncome;

    @JsonProperty("status")
    private String status;

    @JsonProperty("order_details")
    private List<OrderDetailResponse> orderDetails;

    public static OrderShopResponse fromOrderShop(OrderShop orderShop) {
        OrderShopResponse response = OrderShopResponse.builder()
                .id(orderShop.getId())
                .parentOrderId(orderShop.getParentOrder() != null ? orderShop.getParentOrder().getId() : null)
                .shopId(orderShop.getShop() != null ? orderShop.getShop().getId() : null)
                .shopName(orderShop.getShop() != null ? orderShop.getShop().getName() : null)
                .shippingMethod(orderShop.getShippingMethod())
                .shippingFee(orderShop.getShippingFee())
                .subTotal(orderShop.getSubTotal())
                .adminCommission(orderShop.getAdminCommission())
                .shopIncome(orderShop.getShopIncome())
                .status(orderShop.getStatus() != null ? orderShop.getStatus().name() : null)
                .build();

        response.setCreatedAt(orderShop.getCreatedAt());
        response.setUpdatedAt(orderShop.getUpdatedAt());

        return response;
    }

}
