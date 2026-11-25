package cafe.domain.menu;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * 음료 공통 부모. 사이즈·온도를 가지며, 만들 수 있는 온도는 자식 클래스가 생성자로 정한다.
 * 가격 계산(sizeSurcharge)은 자식이 오버라이딩해서 바꿀 수 있다.
 */
public abstract class Beverage extends MenuItem {

    private final Size size;
    private final Temperature temperature;
    private final Set<Temperature> allowed;

    protected Beverage(String id, String name, int basePrice,
                       Size size, Temperature temperature, Set<Temperature> allowed) {
        super(id, name, basePrice);
        if (allowed == null || allowed.isEmpty()) {
            throw new IllegalArgumentException("만들 수 있는 온도가 하나 이상 필요해요.");
        }
        this.allowed = Collections.unmodifiableSet(EnumSet.copyOf(allowed));
        this.size = Objects.requireNonNull(size, "사이즈가 필요해요.");
        if (temperature == null || !this.allowed.contains(temperature)) {
            throw new IllegalArgumentException(name + "은(는) 선택한 온도로 만들 수 없어요.");
        }
        this.temperature = temperature;
    }

    public Size getSize() {
        return size;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    /** 사이즈 추가 요금. 음료마다 다르게 정하고 싶으면 오버라이딩한다. */
    protected int sizeSurcharge() {
        return size.getSurcharge();
    }

    @Override
    public int price() {
        return getBasePrice() + sizeSurcharge();
    }

    @Override
    public String description() {
        return temperature.getLabel() + " " + getName() + " " + size.getLabel();
    }

    @Override
    public boolean supportsSize() {
        return true;
    }

    @Override
    public Set<Temperature> allowedTemperatures() {
        return allowed;
    }

    @Override
    public final MenuItem configure(Size newSize, Temperature newTemperature) {
        return create(newSize == null ? size : newSize,
                newTemperature == null ? temperature : newTemperature);
    }

    /** 같은 메뉴를 다른 사이즈·온도로 새로 만든다. */
    protected abstract Beverage create(Size size, Temperature temperature);
}
