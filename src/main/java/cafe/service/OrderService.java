package cafe.service;

import cafe.common.CafeException;
import cafe.domain.discount.DiscountPolicy;
import cafe.domain.member.Customer;
import cafe.domain.member.GuestMember;
import cafe.domain.menu.MenuItem;
import cafe.domain.menu.Option;
import cafe.domain.order.Order;
import cafe.domain.order.Receipt;
import cafe.domain.payment.Payment;
import cafe.repository.CouponRepository;
import cafe.repository.MemberRepository;
import cafe.repository.MenuRepository;
import cafe.repository.OrderRepository;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 흐름을 연결만 하는 서비스. 가격·할인·적립 계산은 도메인 객체(MenuItem, DiscountPolicy,
 * Member, Order)가 하고, 여기에는 계산이나 종류별 if/else를 두지 않는다.
 */
public class OrderService {

    private final MenuRepository menuRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final CouponRepository couponRepository;
    private final List<DiscountPolicy> automaticPolicies;
    private final Clock clock;

    public OrderService(MenuRepository menuRepository, MemberRepository memberRepository,
                        OrderRepository orderRepository, CouponRepository couponRepository,
                        List<DiscountPolicy> automaticPolicies, Clock clock) {
        this.menuRepository = menuRepository;
        this.memberRepository = memberRepository;
        this.orderRepository = orderRepository;
        this.couponRepository = couponRepository;
        this.automaticPolicies = List.copyOf(automaticPolicies);
        this.clock = clock;
    }

    public Receipt placeOrder(OrderRequest request) {
        // 입력으로 확인할 수 있는 것(회원, 메뉴, 쿠폰)을 먼저 확인하고, 문제가 없을 때 주문번호를 만든다.
        Customer customer = findCustomer(request.memberId());

        List<MenuItem> items = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.items()) {
            MenuItem item = buildItem(itemRequest);
            for (int i = 0; i < itemRequest.quantity(); i++) {
                items.add(item);
            }
        }

        List<DiscountPolicy> policies = new ArrayList<>(automaticPolicies);
        String couponCode = request.couponCode();
        if (couponCode != null && !couponCode.isBlank()) {
            policies.add(couponRepository.findByCode(couponCode.trim())
                    .orElseThrow(() -> new CafeException("존재하지 않는 쿠폰이에요: " + couponCode)));
        }

        Order order = new Order(orderRepository.nextId(), customer, clock);
        items.forEach(order::addItem);

        Payment payment = request.paymentType().create(customer);
        Receipt receipt = order.checkout(payment, policies);
        orderRepository.save(order);
        return receipt;
    }

    public Receipt cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CafeException("존재하지 않는 주문이에요: " + orderId));
        order.cancel();
        return order.receipt();
    }

    /** memberId가 비어 있으면 전체 주문을 돌려준다. */
    public List<Order> findOrders(String memberId) {
        if (memberId == null || memberId.isBlank()) {
            return orderRepository.findAll();
        }
        return orderRepository.findByCustomerId(memberId.trim());
    }

    /** 입력한 회원 번호가 있는지 바로 확인한다. (빈칸 = 비회원이라 통과) 없으면 예외. */
    public void checkMember(String memberId) {
        findCustomer(memberId);
    }

    /** 입력한 쿠폰을 지금 쓸 수 있는지 바로 확인한다. (빈칸 = 쿠폰 없음이라 통과) 안 되면 예외. */
    public void checkCoupon(String couponCode) {
        if (couponCode == null || couponCode.isBlank()) {
            return;
        }
        couponRepository.findByCode(couponCode.trim())
                .orElseThrow(() -> new CafeException("존재하지 않는 쿠폰이에요: " + couponCode))
                .checkUsable();
    }

    private Customer findCustomer(String memberId) {
        if (memberId == null || memberId.isBlank()) {
            return new GuestMember();
        }
        return memberRepository.findById(memberId.trim())
                .orElseThrow(() -> new CafeException("존재하지 않는 회원이에요: " + memberId));
    }

    private MenuItem buildItem(OrderItemRequest request) {
        MenuItem item = menuRepository.findById(request.menuId())
                .orElseThrow(() -> new CafeException("존재하지 않는 메뉴예요: " + request.menuId()))
                .configure(request.size(), request.temperature());
        for (Option option : request.options()) {
            item = option.applyTo(item);
        }
        return item;
    }
}
