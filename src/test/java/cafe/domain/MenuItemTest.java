package cafe.domain;

import cafe.domain.menu.Coffee;
import cafe.domain.menu.Dessert;
import cafe.domain.menu.ExtraShot;
import cafe.domain.menu.MenuItem;
import cafe.domain.menu.Option;
import cafe.domain.menu.PriceLine;
import cafe.domain.menu.Size;
import cafe.domain.menu.Smoothie;
import cafe.domain.menu.Syrup;
import cafe.domain.menu.Temperature;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuItemTest {

    @Test
    void 커피_가격에는_사이즈_추가요금이_포함된다() {
        MenuItem latte = new Coffee("C1", "아메리카노", 3000, Size.L, Temperature.ICE);
        assertEquals(4000, latte.price());
    }

    @Test
    void 스무디는_L사이즈에_추가요금이_더_붙는다_오버라이딩() {
        MenuItem smoothie = new Smoothie("S1", "딸기 스무디", 4500, Size.L, Temperature.ICE);
        assertEquals(4500 + 1000 + 500, smoothie.price());
    }

    @Test
    void 스무디는_핫으로_만들_수_없다() {
        assertThrows(IllegalArgumentException.class,
                () -> new Smoothie("S1", "딸기 스무디", 4500, Size.M, Temperature.HOT));
    }

    @Test
    void 옵션을_겹쳐서_붙이면_가격과_설명이_누적된다() {
        MenuItem coffee = new Coffee("C1", "아메리카노", 3000, Size.M, Temperature.HOT);
        MenuItem decorated = new Syrup(new ExtraShot(coffee));

        assertEquals(3500 + 500 + 300, decorated.price());
        assertTrue(decorated.description().contains("샷 추가"));
        assertTrue(decorated.description().contains("시럽"));
    }

    @Test
    void 영수증_줄은_옵션마다_하나씩_늘어난다() {
        MenuItem coffee = new Coffee("C1", "아메리카노", 3000, Size.S, Temperature.ICE);
        List<PriceLine> lines = new Syrup(new ExtraShot(coffee)).priceLines();

        assertEquals(3, lines.size());
        assertEquals(500, lines.get(1).amount());
        assertEquals(300, lines.get(2).amount());
    }

    @Test
    void 디저트에는_옵션을_붙일_수_없다() {
        MenuItem cake = new Dessert("D1", "치즈케이크", 6000);
        assertThrows(IllegalArgumentException.class, () -> new ExtraShot(cake));
    }

    @Test
    void configure는_원본을_바꾸지_않고_새_항목을_돌려준다() {
        MenuItem prototype = new Coffee("C1", "아메리카노", 3000);   // 기본 M, ICE
        MenuItem large = prototype.configure(Size.L, Temperature.HOT);

        assertEquals(3500, prototype.price());
        assertEquals(4000, large.price());
    }

    @Test
    void 옵션_이름으로_Decorator를_적용한다() {
        MenuItem coffee = new Coffee("C1", "아메리카노", 3000, Size.S, Temperature.ICE);
        MenuItem result = Option.fromLabel("샷추가").applyTo(coffee);
        assertEquals(3500, result.price());
    }

    @Test
    void 알_수_없는_옵션_이름은_예외() {
        assertThrows(IllegalArgumentException.class, () -> Option.fromLabel("초콜릿"));
    }

    @Test
    void 음수_가격은_생성할_수_없다() {
        assertThrows(IllegalArgumentException.class, () -> new Dessert("D1", "쿠키", -1));
    }
}
