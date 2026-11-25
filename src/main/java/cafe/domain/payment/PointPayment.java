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

    @Override
    protected void doPay(int amount) {
        customer.usePoints(amount);
    }

    @Override
    protected void doRefund(int amount) {
        customer.restorePoints(amount);
    }
}
