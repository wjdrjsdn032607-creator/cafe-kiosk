package cafe.domain.order;

import cafe.domain.menu.MenuItem;

/**
 * 주문서의 한 줄: 같은 메뉴(같은 사이즈·온도·옵션)를 몇 개 시켰는지.
 * 예) "아이스 카페라떼 M + 휘핑크림" × 3
 *
 * 영수증과 주문 내역이 같은 메뉴를 수량만큼 여러 줄로 찍지 않고 한 줄로 묶어 보여줄 때 쓴다.
 */
public record OrderLine(MenuItem item, int quantity) {

    public OrderLine {
        if (item == null) {
            throw new IllegalArgumentException("메뉴가 필요해요.");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1 이상이어야 해요.");
        }
    }

    /** 이 줄의 금액 = 한 개 가격 × 수량 */
    public int lineTotal() {
        return item.price() * quantity;
    }
}
