package cafe.domain.discount;

import cafe.common.CafeException;

import java.time.Clock;
import java.time.LocalDate;

/**
 * 쿠폰 공통 부모. 사용 여부(used)는 private이고 결제 성공 시 onApplied(), 주문 취소 시 onCancelled()로만 바뀐다.
 * 할인 금액 계산(calculate)만 자식이 다르게 구현한다.
 */
public abstract class Coupon implements DiscountPolicy {

    private final String code;
    private final LocalDate expiresOn;
    private final Clock clock;
    private boolean used;

    protected Coupon(String code, LocalDate expiresOn, Clock clock) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("쿠폰 코드가 비어 있어요.");
        }
        this.code = code;
        this.expiresOn = expiresOn;
        this.clock = clock;
    }

    public String getCode() {
        return code;
    }

    public boolean isUsed() {
        return used;
    }

    @Override
    public String name() {
        return "쿠폰 (" + code + ")";
    }

    /** 지금 쓸 수 있는 쿠폰인지 확인한다. 이미 썼거나 기간이 지났으면 예외. */
    public void checkUsable() {
        if (used) {
            throw new CafeException("이미 사용한 쿠폰이에요: " + code);
        }
        if (LocalDate.now(clock).isAfter(expiresOn)) {
            throw new CafeException("유효기간이 지난 쿠폰이에요: " + code);
        }
    }

    @Override
    public final int discount(int amount) {
        checkUsable();
        return Math.min(calculate(amount), amount);
    }

    @Override
    public void onApplied() {
        used = true;
    }

    /** 주문이 취소되면 쿠폰을 다시 쓸 수 있게 돌려놓는다. (유효기간이 지났으면 돌려놔도 쓸 수 없다) */
    @Override
    public void onCancelled() {
        used = false;
    }

    /** 쿠폰 종류별 할인 금액 계산. */
    protected abstract int calculate(int amount);
}
