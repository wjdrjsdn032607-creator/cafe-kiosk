package cafe.repository;

import cafe.domain.discount.Coupon;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryCouponRepository implements CouponRepository {

    private final Map<String, Coupon> coupons = new HashMap<>();

    @Override
    public void save(Coupon coupon) {
        coupons.put(coupon.getCode(), coupon);
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        return Optional.ofNullable(coupons.get(code));
    }
}
