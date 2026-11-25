package cafe.domain.payment;

import cafe.domain.member.Customer;

/** 포인트 결제. 포인트 차감·환원은 고객 객체(Customer)가 검증한다. */
public class PointPayment extends Payment {

    private final Customer customer;

    public PointPayment(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String methodName() {
        return "포인트";
    }

    /** 포인트로 낸 금액은 다시 적립하지 않고, 승급용 누적 금액에도 넣지 않는다. */
    @Override
    public int earnableAmount(int paidAmount) {
        return 0;
    }

    @Override
    protected void doPay(int amount) {
        customer.usePoints(amount);
    }

    @Override
    protected void doRefund(int amount) {
        customer.restorePoints(amount);
    }
}
