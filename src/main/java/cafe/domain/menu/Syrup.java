package cafe.domain.menu;

public class Syrup extends OptionDecorator {

    public Syrup(MenuItem inner) {
        super(inner, "시럽", 300);
    }
}
