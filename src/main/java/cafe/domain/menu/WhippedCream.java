package cafe.domain.menu;

public class WhippedCream extends OptionDecorator {

    public WhippedCream(MenuItem inner) {
        super(inner, "휘핑크림", 500);
    }
}
