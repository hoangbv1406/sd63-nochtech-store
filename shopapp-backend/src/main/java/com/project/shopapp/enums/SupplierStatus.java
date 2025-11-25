package com.project.shopapp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SupplierStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String status;

    SupplierStatus(String status) {
        this.status = status;
    }

    @JsonValue
    public String getStatus() {
        return this.status;
    }

    @JsonCreator
    public static SupplierStatus fromString(String text) {
        for (SupplierStatus s : SupplierStatus.values()) {
            if (s.status.equalsIgnoreCase(text)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Trạng thái nhà cung cấp không hợp lệ: " + text);
    }
}
