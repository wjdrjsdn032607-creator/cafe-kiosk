package cafe.domain;

import cafe.common.CafeException;
import cafe.domain.member.GuestMember;
import cafe.domain.member.Member;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberTest {

    @Test
    void 브론즈는_결제금액의_1퍼센트를_적립한다() {
        Member member = new Member("M1", "홍길동");
        int earned = member.earn(10_000);

        assertEquals(100, earned);
        assertEquals(100, member.getPoints());
    }

    @Test
    void 누적_10만원이면_실버로_자동_승급한다() {
        Member member = new Member("M1", "홍길동");
        member.earn(100_000);
        assertEquals("SILVER", member.getGrade().name());
    }

    @Test
    void 누적_30만원이면_골드로_자동_승급한다() {
        Member member = new Member("M1", "홍길동", 0, 290_000);
        assertEquals("SILVER", member.getGrade().name());
        member.earn(10_000);
        assertEquals("GOLD", member.getGrade().name());
    }

    @Test
    void 골드는_1만원_이상_결제하면_보너스_포인트가_붙는다() {
        Member gold = new Member("M1", "이골드", 0, 300_000);
        assertEquals(500 + 100, gold.earn(10_000));
        assertEquals(250, gold.earn(5_000));
    }

    @Test
    void 포인트가_부족하면_사용할_수_없다() {
        Member member = new Member("M1", "홍길동", 500, 0);
        assertThrows(CafeException.class, () -> member.usePoints(501));
        assertEquals(500, member.getPoints());
    }

    @Test
    void 포인트를_사용하면_차감된다() {
        Member member = new Member("M1", "홍길동", 1_000, 0);
        member.usePoints(300);
        assertEquals(700, member.getPoints());
    }

    @Test
    void 적립_취소는_포인트와_누적금액을_되돌린다() {
        Member member = new Member("M1", "홍길동");
        int earned = member.earn(10_000);
        member.reverseEarn(10_000, earned);

        assertEquals(0, member.getPoints());
        assertEquals(0, member.getTotalSpent());
    }

    @Test
    void 비회원은_포인트를_사용할_수_없고_적립도_없다() {
        GuestMember guest = new GuestMember();
        assertEquals(0, guest.earn(10_000));
        assertThrows(CafeException.class, () -> guest.usePoints(1));
    }

    @Test
    void 이름이_비어_있으면_회원을_만들_수_없다() {
        assertThrows(IllegalArgumentException.class, () -> new Member("M1", " "));
    }
}
