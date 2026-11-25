package cafe;

import cafe.domain.discount.AmountCoupon;
import cafe.domain.discount.PercentCoupon;
import cafe.domain.member.Member;
import cafe.domain.menu.Coffee;
import cafe.domain.menu.Dessert;
import cafe.domain.menu.Smoothie;
import cafe.domain.menu.Tea;
import cafe.repository.CouponRepository;
import cafe.repository.MemberRepository;
import cafe.repository.MenuRepository;

import java.time.Clock;
import java.time.LocalDate;

/** 시연용 샘플 데이터. 프로그램을 시작할 때 한 번 넣는다. */
public final class SampleData {

    private SampleData() {
    }

    public static void load(MenuRepository menus, MemberRepository members,
                            CouponRepository coupons, Clock clock) {
        menus.save(new Coffee("C01", "아메리카노", 3000));
        menus.save(new Coffee("C02", "카페라떼", 3500));
        menus.save(new Tea("T01", "얼그레이 티", 3000));
        menus.save(new Tea("T02", "유자차", 3500));
        menus.save(new Smoothie("S01", "딸기 스무디", 4500));
        menus.save(new Smoothie("S02", "망고 스무디", 4800));
        menus.save(new Dessert("D01", "치즈케이크", 6000));
        menus.save(new Dessert("D02", "초코 쿠키", 2500));

        members.save(new Member("M001", "홍길동"));                    // BRONZE
        members.save(new Member("M002", "김실버", 2_000, 120_000));     // SILVER
        members.save(new Member("M003", "이골드", 8_000, 350_000));     // GOLD

        LocalDate expires = LocalDate.now(clock).plusYears(1);
        coupons.save(new AmountCoupon("WELCOME500", 500, expires, clock));
        coupons.save(new PercentCoupon("SPECIAL10", 10, expires, clock));
    }
}
