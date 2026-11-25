package cafe.domain.payment;

/**
 * 결제 수단 추상 클래스 (Template Method).
 * 금액 검증 같은 공통 흐름은 pay()/refund()가 맡고(final), 실제 처리는 자식이 doPay()/doRefund()로 구현한다.
 */
public abstract class Payment {

    public final void pay(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("결제 금액은 0원 이상이어야 해요.");
        }
        doPay(amount);
    }

    public final void refund(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("환불 금액은 0원 이상이어야 해요.");
        }
        doRefund(amount);
    }

    public abstract String methodName();

    protected abstract void doPay(int amount);

    protected abstract void doRefund(int amount);
}
