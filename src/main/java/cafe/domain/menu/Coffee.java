package cafe.domain.menu;

import java.util.EnumSet;

/** 커피: 아이스/핫 모두 가능. */
public class Coffee extends Beverage {

    public Coffee(String id, String name, int basePrice) {
        this(id, name, basePrice, Size.M, Temperature.ICE);
    }

    public Coffee(String id, String name, int basePrice, Size size, Temperature temperature) {
        super(id, name, basePrice, size, temperature, EnumSet.of(Temperature.ICE, Temperature.HOT));
    }

    @Override
    protected Beverage create(Size size, Temperature temperature) {
        return new Coffee(getId(), getName(), getBasePrice(), size, temperature);
    }
}
