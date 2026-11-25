package cafe.domain.discount;

import java.time.Clock;
import java.time.LocalTime;

/** 정해진 시간대에만 적용되는 비율 할인. 시계(Clock)를 주입받아서 테스트에서 시간을 고정할 수 있다. */
public class HappyHourDiscount implements DiscountPolicy {

    private final LocalTime start;
    private final LocalTime end;
    private final int percent;
    private final Clock clock;

    public HappyHourDiscount(LocalTime start, LocalTime end, int percent, Clock clock) {
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("시작 시간이 종료 시간보다 빨라야 해요.");
        }
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("할인율은 0~100 사이여야 해요.");
        }
        this.start = start;
        this.end = end;
        this.percent = percent;
        this.clock = clock;
    }

    @Override
    public String name() {
        return "해피아워 할인 (" + percent + "%)";
    }

    @Override
    public int discount(int amount) {
        LocalTime now = LocalTime.now(clock);
        boolean inWindow = !now.isBefore(start) && now.isBefore(end);
        return inWindow ? amount * percent / 100 : 0;
    }
}
