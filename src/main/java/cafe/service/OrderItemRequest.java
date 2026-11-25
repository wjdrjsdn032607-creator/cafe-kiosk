package cafe.service;

import cafe.domain.menu.Option;
import cafe.domain.menu.Size;
import cafe.domain.menu.Temperature;

import java.util.List;

/** 주문 항목 하나에 대한 요청 (UI가 입력받은 값을 서비스로 전달하는 용도). size/temperature는 null 허용. */
public record OrderItemRequest(String menuId, Size size, Temperature temperature,
                               List<Option> options, int quantity) {

    public OrderItemRequest {
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 해요.");
        }
        options = options == null ? List.of() : List.copyOf(options);
    }
}
