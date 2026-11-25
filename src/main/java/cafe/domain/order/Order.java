package cafe.domain.order;

import cafe.common.CafeException;
import cafe.domain.discount.DiscountPolicy;
import cafe.domain.discount.GradeDiscount;
import cafe.domain.member.Customer;
import cafe.domain.menu.MenuItem;
import cafe.domain.payment.Payment;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 주문. 금액 계산, 할인 적용, 결제, 포인트 적립, 취소까지 주문의 규칙은 이 클래스가 직접 처리한다.
 * (서비스는 객체를 연결만 한다.)
 *
 * [은닉성] 상태(status)는 checkout()/cancel()로만 바뀌고, 잘못된 순서의 호출은 예외가 된다.
 * [다형성] MenuItem, DiscountPolicy, Payment, Customer 타입만 알고 구체 클래스는 모른다.
 */
public class Order {

    private final String id;
    private final Customer customer;
    private final LocalDateTime createdAt;
    private final List<MenuItem> items = new ArrayList<>();
    private final List<DiscountLine> discountLines = new ArrayList<>();
    private OrderStatus status = OrderStatus.CREATED;
    private Payment payment;
    private int paidAmount;
    private int earnedPoints;

    public Order(String id, Customer customer, Clock clock) {
        this.id = Objects.requireNonNull(id, "주문번호가 필요해요.");
        this.customer = Objects.requireNonNull(customer, "고객이 필요해요.");
        this.createdAt = LocalDateTime.now(clock);
    }

    public void addItem(MenuItem item) {
        if (status != OrderStatus.CREATED) {
            throw new CafeException("결제가 끝난 주문에는 메뉴를 추가할 수 없어요.");
        }
        items.add(Objects.requireNonNull(item, "메뉴가 필요해요."));
    }

    /** 할인 전 금액 (모든 항목의 price() 합). */
    public int subtotal() {
        return items.stream().mapToInt(MenuItem::price).sum();
    }

    /**
     * 할인을 순서대로 적용하고 결제한 뒤 포인트를 적립한다.
     * 결제가 실패하면 주문 상태와 쿠폰은 그대로 남는다.
     */
    public Receipt checkout(Payment payment, List<DiscountPolicy> extraPolicies) {
        if (status != OrderStatus.CREATED) {
            throw new CafeException("이미 처리된 주문이에요.");
        }
        if (items.isEmpty()) {
            throw new CafeException("주문할 메뉴가 없어요.");
        }

        List<DiscountPolicy> policies = new ArrayList<>();
        policies.add(new GradeDiscount(customer.getGrade()));
        policies.addAll(extraPolicies);

        List<DiscountLine> lines = new ArrayList<>();
        int running = subtotal();
        for (DiscountPolicy policy : policies) {
            int discount = Math.min(policy.discount(running), running);
            if (discount > 0) {
                lines.add(new DiscountLine(policy.name(), discount));
                running -= discount;
            }
        }

        payment.pay(running);          // 실패하면 여기서 예외, 아래는 실행되지 않는다.
        policies.forEach(DiscountPolicy::onApplied);

        this.discountLines.addAll(lines);
        this.payment = payment;
        this.paidAmount = running;
        this.earnedPoints = customer.earn(running);
        this.status = OrderStatus.PAID;
        return new Receipt(this);
    }

    /** 결제 완료 주문만 취소할 수 있다. 환불하고 적립 포인트를 되돌린다. */
    public void cancel() {
        if (status == OrderStatus.CANCELLED) {
            throw new CafeException("이미 취소된 주문이에요.");
        }
        if (status != OrderStatus.PAID) {
            throw new CafeException("결제 전 주문은 취소할 수 없어요.");
        }
        payment.refund(paidAmount);
        customer.reverseEarn(paidAmount, earnedPoints);
        status = OrderStatus.CANCELLED;
    }

    public Receipt receipt() {
        return new Receipt(this);
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<MenuItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public List<DiscountLine> getDiscountLines() {
        return Collections.unmodifiableList(discountLines);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public int getPaidAmount() {
        return paidAmount;
    }

    public int getEarnedPoints() {
        return earnedPoints;
    }

    public String getPaymentName() {
        return payment == null ? "-" : payment.methodName();
    }
}
