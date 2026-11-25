package cafe.domain.menu;

import java.util.List;
import java.util.Set;

/**
 * 메뉴(주문 항목)의 최상위 추상 클래스.
 *
 * [추상성] price()/description()은 하위 클래스가 반드시 구현한다.
 * [은닉성] 필드는 private이고, 생성자에서 값을 검증한다.
 * [다형성] Order는 구체 종류(Coffee, Dessert 등)를 모르고 MenuItem 타입으로만 다룬다.
 */
public abstract class MenuItem {

    private final String id;
    private final String name;
    private final int basePrice;

    protected MenuItem(String id, String name, int basePrice) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("메뉴 ID가 비어 있어요.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("메뉴 이름이 비어 있어요.");
        }
        if (basePrice < 0) {
            throw new IllegalArgumentException("메뉴 가격은 0원 이상이어야 해요.");
        }
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getBasePrice() {
        return basePrice;
    }

    /** 이 항목의 최종 가격 (사이즈, 옵션 포함). */
    public abstract int price();

    /** 주문 내역에 보여줄 설명. */
    public abstract String description();

    /** 영수증에 찍을 줄들. 옵션이 붙으면 줄이 늘어난다. */
    public List<PriceLine> priceLines() {
        return List.of(new PriceLine(description(), price()));
    }

    /** 사이즈·온도를 정한 새 항목을 돌려준다. 사이즈 개념이 없는 메뉴는 자기 자신을 돌려준다. */
    public MenuItem configure(Size size, Temperature temperature) {
        return this;
    }

    public boolean supportsSize() {
        return false;
    }

    public boolean supportsOptions() {
        return true;
    }

    public Set<Temperature> allowedTemperatures() {
        return Set.of();
    }
}
