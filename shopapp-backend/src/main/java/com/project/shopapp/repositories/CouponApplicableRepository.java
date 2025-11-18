package com.project.shopapp.repositories;

import com.project.shopapp.models.CouponApplicable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponApplicableRepository extends JpaRepository<CouponApplicable, Long> {
    List<CouponApplicable> findByCouponId(Long couponId);
}
