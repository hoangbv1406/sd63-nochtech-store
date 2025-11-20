package com.project.shopapp.services.coupon;

import com.project.shopapp.enums.DiscountType;
import com.project.shopapp.models.Coupon;
import com.project.shopapp.repositories.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;

    @Override
    public BigDecimal calculateCouponValue(String couponCode, BigDecimal totalAmount) {
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));

        if (!coupon.isActive()) {
            throw new IllegalArgumentException("Coupon is not active");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartDate())) {
            throw new IllegalArgumentException("Coupon is not yet valid");
        }
        if (now.isAfter(coupon.getEndDate())) {
            throw new IllegalArgumentException("Coupon has expired");
        }

        BigDecimal minAmount = coupon.getMinOrderAmount();
        if (minAmount != null && totalAmount.compareTo(minAmount) < 0) {
            throw new IllegalArgumentException("Total amount must be at least " + minAmount + " to use this coupon");
        }

        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal discountValue = coupon.getDiscountValue();

        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = totalAmount.multiply(discountValue).divide(BigDecimal.valueOf(100));
            BigDecimal maxDiscount = coupon.getMaxDiscountAmount();
            if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
                discount = maxDiscount;
            }
        } else if (coupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            discount = discountValue;
        }

        if (discount.compareTo(totalAmount) > 0) {
            discount = totalAmount;
        }

        return discount;
    }

}
