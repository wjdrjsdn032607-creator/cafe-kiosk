package cafe.domain.discount;

/**
 * 할인 규약(인터페이스). 등급 할인, 쿠폰(정액·정률)이 각자 다른 방식으로 구현한다.
 * Order는 어떤 할인인지 모르고 discount()만 호출한다.
 */
public interface DiscountPolicy {

    /** 영수증에 표시할 할인 이름. */
    String name();

    /** 현재 금액(amount)에서 깎을 금액. 적용할 수 없으면 0. */
    int discount(int amount);

    /** 결제가 성공한 뒤 호출된다. 쿠폰처럼 사용 처리가 필요한 할인만 오버라이딩한다. */
    default void onApplied() {
    }

    /** 결제한 주문이 취소된 뒤 호출된다. onApplied()에서 한 일을 되돌릴 할인만 오버라이딩한다. */
    default void onCancelled() {
    }
}
