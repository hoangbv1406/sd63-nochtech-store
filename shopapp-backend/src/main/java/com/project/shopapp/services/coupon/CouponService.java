package com.project.shopapp.services.coupon;

import java.math.BigDecimal;

public interface CouponService {
    BigDecimal calculateCouponValue(String couponCode, BigDecimal totalAmount);
}
