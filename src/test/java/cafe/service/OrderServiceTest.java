package cafe.service;

import cafe.SampleData;
import cafe.common.CafeException;
import cafe.domain.discount.DiscountPolicy;
import cafe.domain.member.Member;
import cafe.domain.menu.Option;
import cafe.domain.menu.Size;
import cafe.domain.menu.Temperature;
import cafe.domain.order.Order;
import cafe.domain.order.OrderStatus;
import cafe.domain.order.Receipt;
import cafe.domain.payment.PaymentType;
import cafe.repository.InMemoryCouponRepository;
import cafe.repository.InMemoryMemberRepository;
import cafe.repository.InMemoryMenuRepository;
import cafe.repository.InMemoryOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 서비스가 객체들을 올바르게 연결하는지 확인하는 통합 테스트 (자동 할인 없이 계산을 고정). */
class OrderServiceTest {

    private InMemoryMemberRepository members;
    private InMemoryOrderRepository orders;
    private OrderService orderService;
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.systemDefaultZone();
        InMemoryMenuRepository menus = new InMemoryMenuRepository();
        members = new InMemoryMemberRepository();
        orders = new InMemoryOrderRepository();
        InMemoryCouponRepository coupons = new InMemoryCouponRepository();
        SampleData.load(menus, members, coupons, clock);

        List<DiscountPolicy> noAutomaticPolicies = List.of();
        orderService = new OrderService(menus, members, orders, coupons, noAutomaticPolicies, clock);
        memberService = new MemberService(members);
    }

    private OrderRequest americanoLarge(String memberId, String coupon, PaymentType payment) {
        OrderItemRequest item = new OrderItemRequest("C01", Size.L, Temperature.ICE,
                List.of(Option.EXTRA_SHOT, Option.SYRUP), 1);
        return new OrderRequest(memberId, List.of(item), coupon, payment);
    }

    @Test
    void 골드_회원_주문_흐름이_끝까지_동작한다() {
        Receipt receipt = orderService.placeOrder(americanoLarge("M003", "WELCOME500", PaymentType.CARD));

        assertEquals(4_060, receipt.getPaidAmount());
        assertEquals(203, receipt.getEarnedPoints());
        assertEquals(8_203, members.findById("M003").orElseThrow().getPoints());
    }

    @Test
    void 비회원도_카드로_주문할_수_있고_할인과_적립이_없다() {
        Receipt receipt = orderService.placeOrder(americanoLarge("", "", PaymentType.CARD));

        assertEquals(4_800, receipt.getPaidAmount());
        assertEquals(0, receipt.getEarnedPoints());
    }

    @Test
    void 수량만큼_항목이_추가된다() {
        OrderItemRequest cookies = new OrderItemRequest("D02", null, null, List.of(), 3);
        Receipt receipt = orderService.placeOrder(new OrderRequest("", List.of(cookies), "", PaymentType.CARD));

        assertEquals(7_500, receipt.getPaidAmount());
    }

    @Test
    void 없는_회원_메뉴_쿠폰은_예외이고_주문번호도_소비하지_않는다() {
        assertThrows(CafeException.class,
                () -> orderService.placeOrder(americanoLarge("M999", "", PaymentType.CARD)));
        assertThrows(CafeException.class,
                () -> orderService.placeOrder(americanoLarge("", "NOPE", PaymentType.CARD)));
        OrderItemRequest unknown = new OrderItemRequest("X99", null, null, List.of(), 1);
        assertThrows(CafeException.class,
                () -> orderService.placeOrder(new OrderRequest("", List.of(unknown), "", PaymentType.CARD)));

        Receipt receipt = orderService.placeOrder(americanoLarge("", "", PaymentType.CARD));
        assertEquals("O-0001", receipt.getOrderId());
    }

    @Test
    void 쿠폰은_한_번만_사용할_수_있다() {
        orderService.placeOrder(americanoLarge("M001", "WELCOME500", PaymentType.CARD));
        assertThrows(CafeException.class,
                () -> orderService.placeOrder(americanoLarge("M001", "WELCOME500", PaymentType.CARD)));
    }

    @Test
    void 디저트에_옵션을_요청하면_실패한다() {
        OrderItemRequest cake = new OrderItemRequest("D01", null, null, List.of(Option.SYRUP), 1);
        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(new OrderRequest("", List.of(cake), "", PaymentType.CARD)));
    }

    @Test
    void 비회원이_포인트로_결제하면_실패한다() {
        assertThrows(CafeException.class,
                () -> orderService.placeOrder(americanoLarge("", "", PaymentType.POINT)));
    }

    @Test
    void 쿠폰을_쓴_주문을_취소하면_같은_쿠폰으로_다시_주문할_수_있다() {
        Receipt receipt = orderService.placeOrder(americanoLarge("M001", "WELCOME500", PaymentType.CARD));
        orderService.cancelOrder(receipt.getOrderId());

        orderService.checkCoupon("WELCOME500");   // 예외 없이 통과
        Receipt again = orderService.placeOrder(americanoLarge("M001", "WELCOME500", PaymentType.CARD));
        assertTrue(again.format().contains("WELCOME500"));
    }

    @Test
    void 주문을_취소하면_상태가_바뀌고_포인트가_환원된다() {
        Receipt receipt = orderService.placeOrder(americanoLarge("M003", "", PaymentType.CARD));
        orderService.cancelOrder(receipt.getOrderId());

        Order order = orders.findById(receipt.getOrderId()).orElseThrow();
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(8_000, members.findById("M003").orElseThrow().getPoints());
        assertThrows(CafeException.class, () -> orderService.cancelOrder(receipt.getOrderId()));
    }

    @Test
    void 없는_주문은_취소할_수_없다() {
        assertThrows(CafeException.class, () -> orderService.cancelOrder("O-9999"));
    }

    @Test
    void 회원별_주문_내역을_조회한다() {
        orderService.placeOrder(americanoLarge("M003", "", PaymentType.CARD));
        orderService.placeOrder(americanoLarge("", "", PaymentType.CARD));

        assertEquals(1, orderService.findOrders("M003").size());
        assertEquals(2, orderService.findOrders("").size());
    }

    @Test
    void 결제가_실패한_주문은_주문번호를_소비하지_않는다() {
        // M001은 포인트가 0이라 포인트 결제가 실패한다.
        assertThrows(CafeException.class,
                () -> orderService.placeOrder(americanoLarge("M001", "", PaymentType.POINT)));

        Receipt receipt = orderService.placeOrder(americanoLarge("", "", PaymentType.CARD));
        assertEquals("O-0001", receipt.getOrderId());
    }

    @Test
    void 같은_메뉴를_여러_개_시키면_영수증에_한_줄로_수량이_표시된다() {
        OrderItemRequest cookies = new OrderItemRequest("D02", null, null, List.of(), 3);
        Receipt receipt = orderService.placeOrder(new OrderRequest("", List.of(cookies), "", PaymentType.CARD));
        String text = receipt.format();

        assertTrue(text.contains("초코 쿠키 x3"));
        assertTrue(text.contains("7,500"));
        assertEquals(1, text.split("초코 쿠키", -1).length - 1);   // 줄이 하나만 있다
    }

    @Test
    void 회원번호와_쿠폰은_결제_전에_바로_확인할_수_있다() {
        orderService.checkMember("");          // 비회원은 통과
        orderService.checkMember("M003");      // 있는 회원은 통과
        assertThrows(CafeException.class, () -> orderService.checkMember("M0003"));

        orderService.checkCoupon("");          // 쿠폰 없음은 통과
        orderService.checkCoupon("WELCOME500");
        assertThrows(CafeException.class, () -> orderService.checkCoupon("WELCOM500"));

        orderService.placeOrder(americanoLarge("M001", "WELCOME500", PaymentType.CARD));
        assertThrows(CafeException.class, () -> orderService.checkCoupon("WELCOME500"));   // 이미 사용
    }

    @Test
    void 신규_회원은_브론즈로_가입하고_번호가_이어진다() {
        Member member = memberService.register("박신규");

        assertEquals("M004", member.getId());
        assertEquals("BRONZE", member.getGrade().name());
        assertTrue(members.findById("M004").isPresent());
        assertThrows(CafeException.class, () -> memberService.find("M777"));
    }
}
