package cafe.domain.member;

/** 할인 5%, 적립 5%, 1만 원 이상 결제하면 보너스 100포인트. 최고 등급이라 승급은 없다. */
public class Gold extends RateBasedGrade {

    static final long THRESHOLD = 300_000L;
    private static final int BONUS_MIN_AMOUNT = 10_000;
    private static final int BONUS_POINTS = 100;

    public Gold() {
        super("GOLD", 5, 5);
    }

    @Override
    public int earnPoints(int paidAmount) {
        int bonus = paidAmount >= BONUS_MIN_AMOUNT ? BONUS_POINTS : 0;
        return super.earnPoints(paidAmount) + bonus;
    }

    @Override
    public MembershipGrade evaluate(long totalSpent) {
        return this;
    }
}
