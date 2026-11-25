package cafe.domain.order;

/** 실제로 적용된 할인 한 줄 (이름과 할인 금액). */
public record DiscountLine(String name, int amount) {
}
