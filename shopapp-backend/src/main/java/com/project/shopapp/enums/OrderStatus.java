package com.project.shopapp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    PENDING("pending"),
    PROCESSING("processing"),
    SHIPPED("shipped"),
    DELIVERED("delivered"),
    CANCELLED("cancelled"),
    RETURNED("returned"),
    REFUNDED("refunded");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    @JsonValue
    public String getStatus() {
        return this.status;
    }

    @JsonCreator
    public static OrderStatus fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái đơn hàng không được để trống");
        }
        for (OrderStatus b : OrderStatus.values()) {
            if (b.status.equalsIgnoreCase(text.trim())) {
                return b;
            }
        }
        throw new IllegalArgumentException("Trạng thái đơn hàng không hợp lệ: " + text);
    }
}
