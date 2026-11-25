package cafe.domain.discount;

import java.time.Clock;
import java.time.LocalDate;

/** 정액 쿠폰 (예: 500원 할인). */
public class AmountCoupon extends Coupon {

    private final int amountOff;

    public AmountCoupon(String code, int amountOff, LocalDate expiresOn, Clock clock) {
        super(code, expiresOn, clock);
        if (amountOff <= 0) {
            throw new IllegalArgumentException("할인 금액은 1원 이상이어야 해요.");
        }
        this.amountOff = amountOff;
    }

    @Override
    protected int calculate(int amount) {
        return amountOff;
    }
}
