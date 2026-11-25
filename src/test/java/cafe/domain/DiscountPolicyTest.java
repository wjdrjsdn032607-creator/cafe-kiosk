package cafe.domain;

import cafe.common.CafeException;
import cafe.domain.discount.AmountCoupon;
import cafe.domain.discount.Coupon;
import cafe.domain.discount.DiscountPolicy;
import cafe.domain.discount.GradeDiscount;
import cafe.domain.discount.HappyHourDiscount;
import cafe.domain.discount.PercentCoupon;
import cafe.domain.member.Gold;
import cafe.domain.member.Silver;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscountPolicyTest {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private static Clock clockAt(String dateTime) {
        return Clock.fixed(LocalDateTime.parse(dateTime).atZone(ZONE).toInstant(), ZONE);
    }

    @Test
    void 등급_할인은_등급_객체가_계산한다() {
        assertEquals(300, new GradeDiscount(new Silver()).discount(10_000));
        assertEquals(500, new GradeDiscount(new Gold()).discount(10_000));
    }

    @Test
    void 정액_쿠폰은_결제금액보다_많이_깎지_않는다() {
        Coupon coupon = new AmountCoupon("A", 5_000, LocalDate.of(2026, 12, 31), clockAt("2026-11-05T10:00:00"));
        assertEquals(3_000, coupon.discount(3_000));
    }

    @Test
    void 정률_쿠폰은_비율로_계산한다() {
        Coupon coupon = new PercentCoupon("P", 10, LocalDate.of(2026, 12, 31), clockAt("2026-11-05T10:00:00"));
        assertEquals(1_000, coupon.discount(10_000));
    }

    @Test
    void 유효기간이_지난_쿠폰은_사용할_수_없다() {
        Coupon coupon = new AmountCoupon("A", 500, LocalDate.of(2026, 11, 1), clockAt("2026-11-05T10:00:00"));
        CafeException e = assertThrows(CafeException.class, () -> coupon.discount(5_000));
        assertTrue(e.getMessage().contains("유효기간"));
    }

    @Test
    void 사용한_쿠폰은_다시_사용할_수_없다() {
        Coupon coupon = new AmountCoupon("A", 500, LocalDate.of(2026, 12, 31), clockAt("2026-11-05T10:00:00"));
        coupon.onApplied();
        assertThrows(CafeException.class, () -> coupon.discount(5_000));
    }

    @Test
    void 해피아워는_정해진_시간대에만_할인한다() {
        LocalTime start = LocalTime.of(14, 0);
        LocalTime end = LocalTime.of(16, 0);

        DiscountPolicy inside = new HappyHourDiscount(start, end, 10, clockAt("2026-11-05T14:30:00"));
        DiscountPolicy outside = new HappyHourDiscount(start, end, 10, clockAt("2026-11-05T17:00:00"));
        DiscountPolicy atEnd = new HappyHourDiscount(start, end, 10, clockAt("2026-11-05T16:00:00"));

        assertEquals(1_000, inside.discount(10_000));
        assertEquals(0, outside.discount(10_000));
        assertEquals(0, atEnd.discount(10_000));
    }

    @Test
    void 해피아워는_시작이_종료보다_빨라야_한다() {
        assertThrows(IllegalArgumentException.class,
                () -> new HappyHourDiscount(LocalTime.of(16, 0), LocalTime.of(14, 0), 10, Clock.systemDefaultZone()));
    }
}
