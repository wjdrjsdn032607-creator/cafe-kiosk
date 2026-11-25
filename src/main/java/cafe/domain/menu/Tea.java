package cafe.domain.menu;

import java.util.EnumSet;

/** 차: 아이스/핫 모두 가능. */
public class Tea extends Beverage {

    public Tea(String id, String name, int basePrice) {
        this(id, name, basePrice, Size.M, Temperature.HOT);
    }

    public Tea(String id, String name, int basePrice, Size size, Temperature temperature) {
        super(id, name, basePrice, size, temperature, EnumSet.of(Temperature.ICE, Temperature.HOT));
    }

    @Override
    protected Beverage create(Size size, Temperature temperature) {
        return new Tea(getId(), getName(), getBasePrice(), size, temperature);
    }
}
