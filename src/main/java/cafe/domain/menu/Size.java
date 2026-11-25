package cafe.domain.menu;

/** 음료 사이즈. 기본 가격은 S 기준이고, 사이즈마다 추가 요금이 붙는다. */
public enum Size {
    S("S", 0),
    M("M", 500),
    L("L", 1000);

    private final String label;
    private final int surcharge;

    Size(String label, int surcharge) {
        this.label = label;
        this.surcharge = surcharge;
    }

    public String getLabel() {
        return label;
    }

    public int getSurcharge() {
        return surcharge;
    }

    public static Size from(String text) {
        if (text != null) {
            for (Size size : values()) {
                if (size.label.equalsIgnoreCase(text.trim())) {
                    return size;
                }
            }
        }
        throw new IllegalArgumentException("알 수 없는 사이즈예요: " + text);
    }
}
