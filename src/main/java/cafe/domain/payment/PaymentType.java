package cafe.domain.payment;

import cafe.domain.member.Customer;

import java.util.function.Function;

/** 사용자가 고르는 결제 수단과 Payment 구현 클래스를 연결한다. 새 수단은 한 줄 추가. */
public enum PaymentType {
    CARD("카드", customer -> new CardPayment("KIOSK-CARD", 1_000_000)),
    POINT("포인트", PointPayment::new);

    private final String label;
    private final Function<Customer, Payment> factory;

    PaymentType(String label, Function<Customer, Payment> factory) {
        this.label = label;
        this.factory = factory;
    }

    public String getLabel() {
        return label;
    }

    public Payment create(Customer customer) {
        return factory.apply(customer);
    }
}
