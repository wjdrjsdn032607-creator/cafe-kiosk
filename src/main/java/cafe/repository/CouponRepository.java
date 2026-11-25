package cafe.repository;

import cafe.domain.discount.Coupon;

import java.util.Optional;

public interface CouponRepository {

    void save(Coupon coupon);

    Optional<Coupon> findByCode(String code);
}
