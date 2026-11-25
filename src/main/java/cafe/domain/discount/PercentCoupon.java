package cafe.domain.discount;

import java.time.Clock;
import java.time.LocalDate;

/** 정률 쿠폰 (예: 10% 할인). */
public class PercentCoupon extends Coupon {

    private final int percent;

    public PercentCoupon(String code, int percent, LocalDate expiresOn, Clock clock) {
        super(code, expiresOn, clock);
        if (percent <= 0 || percent > 100) {
            throw new IllegalArgumentException("할인율은 1~100 사이여야 해요.");
        }
        this.percent = percent;
    }

    @Override
    protected int calculate(int amount) {
        return amount * percent / 100;
    }
}
