package cafe.domain.payment;

import cafe.common.CafeException;

/** 카드 결제 (모의). 한도를 넘으면 결제가 거절된다. */
public class CardPayment extends Payment {

    private final String cardLabel;
    private final int limit;

    public CardPayment(String cardLabel, int limit) {
        this.cardLabel = cardLabel;
        this.limit = limit;
    }

    @Override
    public String methodName() {
        return "카드";
    }

    @Override
    protected void doPay(int amount) {
        if (amount > limit) {
            throw new CafeException("카드 한도를 초과했어요. (" + cardLabel + ")");
        }
    }

    @Override
    protected void doRefund(int amount) {
        // 모의 카드라서 별도 처리 없이 환불 성공으로 본다.
    }
}
