package cafe.domain.menu;

import java.util.function.UnaryOperator;

/**
 * 사용자가 입력하는 옵션 이름과 Decorator 클래스를 연결한다.
 * 새 옵션은 OptionDecorator 하위 클래스를 만들고 여기에 한 줄 추가하면 된다.
 */
public enum Option {
    EXTRA_SHOT("샷추가", ExtraShot::new),
    SYRUP("시럽", Syrup::new),
    WHIPPED_CREAM("휘핑", WhippedCream::new);

    private final String label;
    private final UnaryOperator<MenuItem> decorator;

    Option(String label, UnaryOperator<MenuItem> decorator) {
        this.label = label;
        this.decorator = decorator;
    }

    public String getLabel() {
        return label;
    }

    public MenuItem applyTo(MenuItem item) {
        return decorator.apply(item);
    }

    public static Option fromLabel(String text) {
        if (text != null) {
            String normalized = text.replace(" ", "");
            for (Option option : values()) {
                if (option.label.equals(normalized)) {
                    return option;
                }
            }
        }
        throw new IllegalArgumentException("알 수 없는 옵션이에요: " + text);
    }
}
