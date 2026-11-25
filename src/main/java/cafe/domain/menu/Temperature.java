package cafe.domain.menu;

public enum Temperature {
    ICE("아이스"),
    HOT("핫");

    private final String label;

    Temperature(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
