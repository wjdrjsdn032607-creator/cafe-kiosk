package cafe.domain.order;

import cafe.common.CafeException;
import cafe.domain.discount.DiscountPolicy;
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
 * 주문 상태, 결제, 포인트 적립 및 취소를 관리한다. 할인 계산은 DiscountCalculator에 위임한다.
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
    private final List<DiscountPolicy> appliedPolicies = new ArrayList<>();   // 취소할 때 되돌리기 위해 기억
    private OrderStatus status = OrderStatus.CREATED;
    private Payment payment;
    private int paidAmount;
    private int earnedPoints;
    private String gradeAtOrder;   // 결제할 때의 등급 이름. 나중에 승급·강등돼도 영수증은 이 값을 쓴다.

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

        String gradeName = customer.getGrade().name();

        DiscountCalculator.Result result =
                DiscountCalculator.calculate(
                        subtotal(), customer, extraPolicies);

        int running = result.finalAmount();
        List<DiscountLine> lines = result.lines();
        List<DiscountPolicy> policies = result.policies();

        payment.pay(running);          // 실패하면 여기서 예외, 아래는 실행되지 않는다.
        policies.forEach(DiscountPolicy::onApplied);

        this.discountLines.addAll(lines);
        this.appliedPolicies.addAll(policies);
        this.payment = payment;
        this.paidAmount = running;
        // 적립 기준 금액은 결제 수단이 정한다. (카드: 전액, 포인트: 0원) → Order는 결제 수단 종류를 몰라도 됨
        this.earnedPoints = customer.earn(payment.earnableAmount(running));
        this.gradeAtOrder = gradeName;
        this.status = OrderStatus.PAID;
        return new Receipt(this);
    }

    /** 결제 완료 주문만 취소할 수 있다. 환불하고, 적립 포인트와 사용한 쿠폰을 되돌린다. */
    public void cancel() {
        if (status == OrderStatus.CANCELLED) {
            throw new CafeException("이미 취소된 주문이에요.");
        }
        if (status != OrderStatus.PAID) {
            throw new CafeException("결제 전 주문은 취소할 수 없어요.");
        }
        payment.refund(paidAmount);
        customer.reverseEarn(payment.earnableAmount(paidAmount), earnedPoints);
        appliedPolicies.forEach(DiscountPolicy::onCancelled);   // 쿠폰이면 다시 쓸 수 있게 됨
        status = OrderStatus.CANCELLED;
    }

    public Receipt receipt() {
        return new Receipt(this);
    }

    public String getId() {
        return id;
    }

    /** 주문(결제)했을 때의 등급 이름. 결제 전이면 지금 등급을 돌려준다. */
    public String getGradeAtOrder() {
        return gradeAtOrder != null ? gradeAtOrder : customer.getGrade().name();
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

    /**
     * 같은 메뉴가 연달아 담긴 것을 한 줄로 묶어서 돌려준다. (수량 표시용)
     * 주문할 때 수량 3이면 같은 MenuItem 객체가 3번 담기므로, 같은 객체끼리 묶으면 된다.
     */
    public List<OrderLine> getLines() {
        List<OrderLine> lines = new ArrayList<>();
        MenuItem current = null;
        int count = 0;
        for (MenuItem item : items) {
            if (item == current) {
                count++;
            } else {
                if (current != null) {
                    lines.add(new OrderLine(current, count));
                }
                current = item;
                count = 1;
            }
        }
        if (current != null) {
            lines.add(new OrderLine(current, count));
        }
        return Collections.unmodifiableList(lines);
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
