package cafe.service;

import cafe.domain.payment.PaymentType;

import java.util.List;

/** 주문 한 건에 대한 요청. memberId와 couponCode는 비어 있어도 된다. */
public record OrderRequest(String memberId, List<OrderItemRequest> items,
                           String couponCode, PaymentType paymentType) {

    public OrderRequest {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("주문할 메뉴를 하나 이상 담아 주세요.");
        }
        if (paymentType == null) {
            throw new IllegalArgumentException("결제 수단을 선택해 주세요.");
        }
        items = List.copyOf(items);
    }
}
