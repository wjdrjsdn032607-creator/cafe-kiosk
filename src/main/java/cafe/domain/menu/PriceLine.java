package cafe.domain.menu;

/** 영수증의 한 줄 (이름과 금액). */
public record PriceLine(String label, int amount) {
}
