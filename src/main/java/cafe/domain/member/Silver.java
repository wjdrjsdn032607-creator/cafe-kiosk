package cafe.domain.member;

/** 할인 3%, 적립 3%. 누적 30만 원이면 Gold. */
public class Silver extends RateBasedGrade {

    static final long THRESHOLD = 100_000L;

    public Silver() {
        super("SILVER", 3, 3);
    }

    @Override
    public MembershipGrade evaluate(long totalSpent) {
        return totalSpent >= Gold.THRESHOLD ? new Gold() : this;
    }
}
