package cafe.domain.menu;

import java.util.EnumSet;

/**
 * 스무디: 아이스만 가능하고, L 사이즈는 블렌더 비용으로 500원이 더 붙는다.
 * (부모의 sizeSurcharge()를 오버라이딩해서 가격 규칙만 바꾼다)
 */
public class Smoothie extends Beverage {

    private static final int LARGE_EXTRA = 500;

    public Smoothie(String id, String name, int basePrice) {
        this(id, name, basePrice, Size.M, Temperature.ICE);
    }

    public Smoothie(String id, String name, int basePrice, Size size, Temperature temperature) {
        super(id, name, basePrice, size, temperature, EnumSet.of(Temperature.ICE));
    }

    @Override
    protected int sizeSurcharge() {
        return super.sizeSurcharge() + (getSize() == Size.L ? LARGE_EXTRA : 0);
    }

    @Override
    protected Beverage create(Size size, Temperature temperature) {
        return new Smoothie(getId(), getName(), getBasePrice(), size, temperature);
    }
}
