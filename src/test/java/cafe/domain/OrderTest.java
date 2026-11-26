package cafe.domain;

import cafe.common.CafeException;
import cafe.domain.discount.AmountCoupon;
import cafe.domain.discount.Coupon;
import cafe.domain.member.Customer;
import cafe.domain.member.GuestMember;
import cafe.domain.member.Member;
import cafe.domain.menu.Coffee;
import cafe.domain.menu.Dessert;
import cafe.domain.menu.ExtraShot;
import cafe.domain.menu.MenuItem;
import cafe.domain.menu.Size;
import cafe.domain.menu.Syrup;
import cafe.domain.menu.Temperature;
import cafe.domain.order.Order;
import cafe.domain.order.OrderStatus;
import cafe.domain.order.Receipt;
import cafe.domain.payment.CardPayment;
import cafe.domain.payment.PointPayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderTest {

    private final Clock clock = Clock.systemDefaultZone();
    private Member gold;

    @BeforeEach
    void setUp() {
        gold = new Member("M003", "이골드", 8_000, 350_000);
    }

    private MenuItem largeAmericanoWithShotAndSyrup() {
        MenuItem coffee = new Coffee("C01", "아메리카노", 3000, Size.L, Temperature.ICE);   // 4,000
        return new Syrup(new ExtraShot(coffee));                                            // 4,800
    }

    @Test
    void 골드_회원이_쿠폰으로_결제하면_할인과_적립이_순서대로_계산된다() {
        Order order = new Order("O-1", gold, clock);
        order.addItem(largeAmericanoWithShotAndSyrup());
        Coupon coupon = new AmountCoupon("WELCOME500", 500, LocalDate.now(clock).plusDays(30), clock);

        Receipt receipt = order.checkout(new CardPayment("TEST", 1_000_000), List.of(coupon));

        // 소계 4,800 - 골드 5%(240) - 쿠폰 500 = 4,060 / 적립 5% = 203
        assertEquals(4_800, order.subtotal());
        assertEquals(4_060, receipt.getPaidAmount());
        assertEquals(203, receipt.getEarnedPoints());
        assertEquals(8_203, gold.getPoints());
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertTrue(coupon.isUsed());
    }

    @Test
    void 영수증에_소계_할인_결제금액이_표시된다() {
        Order order = new Order("O-1", gold, clock);
        order.addItem(largeAmericanoWithShotAndSyrup());
        String text = order.checkout(new CardPayment("TEST", 1_000_000), List.of()).format();

        assertTrue(text.contains("샷 추가"));
        assertTrue(text.contains("GOLD 등급 할인"));
        assertTrue(text.contains("4,560"));
    }

    @Test
    void 취소하면_환불되고_적립_포인트가_되돌아간다() {
        Order order = new Order("O-1", gold, clock);
        order.addItem(largeAmericanoWithShotAndSyrup());
        order.checkout(new CardPayment("TEST", 1_000_000), List.of());

        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(8_000, gold.getPoints());
        assertEquals(350_000, gold.getTotalSpent());
    }

    @Test
    void 취소하면_사용한_쿠폰을_다시_쓸_수_있다() {
        Order order = new Order("O-1", gold, clock);
        order.addItem(largeAmericanoWithShotAndSyrup());
        Coupon coupon = new AmountCoupon("WELCOME500", 500, LocalDate.now(clock).plusDays(30), clock);
        order.checkout(new CardPayment("TEST", 1_000_000), List.of(coupon));
        assertTrue(coupon.isUsed());

        order.cancel();

        assertFalse(coupon.isUsed());
        assertEquals(500, coupon.discount(5_000));   // 다시 할인된다
    }

    @Test
    void 이미_취소한_주문은_다시_취소할_수_없다() {
        Order order = new Order("O-1", gold, clock);
        order.addItem(new Dessert("D1", "쿠키", 2_500));
        order.checkout(new CardPayment("TEST", 1_000_000), List.of());
        order.cancel();

        assertThrows(CafeException.class, order::cancel);
    }

    @Test
    void 영수증에는_주문_당시_등급이_고정된다() {
        Member silver = new Member("M2", "김실버", 0, 120_000);   // SILVER
        Order order = new Order("O-1", silver, clock);
        order.addItem(new Dessert("D1", "케이크", 200_000));
        Receipt receipt = order.checkout(new CardPayment("TEST", 1_000_000), List.of());

        assertEquals("GOLD", silver.getGrade().name());          // 결제 후 승급
        assertTrue(receipt.format().contains("김실버 (SILVER)"));  // 영수증은 주문 당시 등급

        order.cancel();                                            // 취소해도
        assertTrue(order.receipt().format().contains("김실버 (SILVER)"));
    }

    @Test
    void 결제_전_주문은_취소할_수_없다() {
        Order order = new Order("O-1", gold, clock);
        order.addItem(new Dessert("D1", "쿠키", 2_500));
        assertThrows(CafeException.class, order::cancel);
    }

    @Test
    void 포인트로_결제하면_포인트가_차감되고_취소하면_환원된다() {
        Order order = new Order("O-1", gold, clock);
        order.addItem(new Dessert("D1", "쿠키", 2_500));
        order.checkout(new PointPayment(gold), List.of());   // 2,500 - 125 = 2,375 사용, 포인트 결제라 적립 0

        assertEquals(8_000 - 2_375, gold.getPoints());
        assertEquals(350_000, gold.getTotalSpent());           // 포인트 결제는 누적 금액에도 안 들어감

        order.cancel();
        assertEquals(8_000, gold.getPoints());
        assertEquals(350_000, gold.getTotalSpent());
    }

    @Test
    void 포인트가_부족하면_결제가_실패하고_주문과_쿠폰은_그대로다() {
        Member poor = new Member("M9", "가난", 100, 0);
        Order order = new Order("O-1", poor, clock);
        order.addItem(new Dessert("D1", "케이크", 6_000));
        Coupon coupon = new AmountCoupon("C", 500, LocalDate.now(clock).plusDays(1), clock);

        assertThrows(CafeException.class,
                () -> order.checkout(new PointPayment(poor), List.of(coupon)));

        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertFalse(coupon.isUsed());
        assertEquals(100, poor.getPoints());
    }

    @Test
    void 비회원은_포인트로_결제할_수_없다() {
        Customer guest = new GuestMember();
        Order order = new Order("O-1", guest, clock);
        order.addItem(new Dessert("D1", "쿠키", 2_500));

        assertThrows(CafeException.class,
                () -> order.checkout(new PointPayment(guest), List.of()));
    }

    @Test
    void 메뉴가_없는_주문은_결제할_수_없다() {
        Order order = new Order("O-1", gold, clock);
        assertThrows(CafeException.class,
                () -> order.checkout(new CardPayment("TEST", 1_000_000), List.of()));
    }

    @Test
    void 결제가_끝난_주문에는_메뉴를_추가할_수_없다() {
        Order order = new Order("O-1", gold, clock);
        order.addItem(new Dessert("D1", "쿠키", 2_500));
        order.checkout(new CardPayment("TEST", 1_000_000), List.of());

        assertThrows(CafeException.class, () -> order.addItem(new Dessert("D2", "마카롱", 3_000)));
    }

    @Test
    void 카드_한도를_넘으면_결제가_거절된다() {
        Order order = new Order("O-1", gold, clock);
        order.addItem(new Dessert("D1", "케이크", 6_000));
        assertThrows(CafeException.class,
                () -> order.checkout(new CardPayment("TEST", 1_000), List.of()));
    }
}
