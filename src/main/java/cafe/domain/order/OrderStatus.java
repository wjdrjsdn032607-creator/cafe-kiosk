package cafe.domain.order;

public enum OrderStatus {
    CREATED("주문 생성"),
    PAID("결제 완료"),
    CANCELLED("취소됨");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
