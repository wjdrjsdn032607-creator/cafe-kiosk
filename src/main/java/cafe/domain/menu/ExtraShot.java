package cafe.domain.menu;

public class ExtraShot extends OptionDecorator {

    public ExtraShot(MenuItem inner) {
        super(inner, "샷 추가", 500);
    }
}
