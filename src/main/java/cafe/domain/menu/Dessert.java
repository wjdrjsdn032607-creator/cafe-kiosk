package cafe.domain.menu;

/** 디저트: 사이즈·온도·옵션이 없다. */
public class Dessert extends MenuItem {

    public Dessert(String id, String name, int basePrice) {
        super(id, name, basePrice);
    }

    @Override
    public int price() {
        return getBasePrice();
    }

    @Override
    public String description() {
        return getName();
    }

    @Override
    public boolean supportsOptions() {
        return false;
    }
}
