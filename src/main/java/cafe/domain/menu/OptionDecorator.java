package cafe.domain.menu;

import java.util.ArrayList;
import java.util.List;

/**
 * 옵션(샷 추가, 시럽 등)을 겹겹이 씌우는 Decorator.
 * 옵션도 MenuItem이라서 Order는 옵션이 붙었는지 모르고 price()만 호출한다.
 * 조합마다 클래스를 만들 필요가 없고, 새 옵션은 이 클래스를 상속한 클래스 하나만 추가하면 된다.
 */
public abstract class OptionDecorator extends MenuItem {

    private final MenuItem inner;
    private final String optionName;
    private final int surcharge;

    protected OptionDecorator(MenuItem inner, String optionName, int surcharge) {
        super(requireInner(inner).getId(), inner.getName(), inner.getBasePrice());
        if (!inner.supportsOptions()) {
            throw new IllegalArgumentException(inner.getName() + "에는 옵션을 추가할 수 없어요.");
        }
        if (surcharge < 0) {
            throw new IllegalArgumentException("옵션 요금은 0원 이상이어야 해요.");
        }
        this.inner = inner;
        this.optionName = optionName;
        this.surcharge = surcharge;
    }

    private static MenuItem requireInner(MenuItem inner) {
        if (inner == null) {
            throw new IllegalArgumentException("옵션을 붙일 메뉴가 필요해요.");
        }
        return inner;
    }

    public String getOptionName() {
        return optionName;
    }

    @Override
    public int price() {
        return inner.price() + surcharge;
    }

    @Override
    public String description() {
        return inner.description() + " + " + optionName;
    }

    @Override
    public List<PriceLine> priceLines() {
        List<PriceLine> lines = new ArrayList<>(inner.priceLines());
        lines.add(new PriceLine("  + " + optionName, surcharge));
        return lines;
    }
}
