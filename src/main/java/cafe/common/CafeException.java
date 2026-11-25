package cafe.common;

/**
 * 업무 규칙을 어겼을 때 던지는 예외 (포인트 부족, 만료된 쿠폰, 이미 취소된 주문 등).
 * 잘못된 인자(음수 가격, 빈 이름 등)는 IllegalArgumentException을 사용한다.
 */
public class CafeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CafeException(String message) {
        super(message);
    }
}
