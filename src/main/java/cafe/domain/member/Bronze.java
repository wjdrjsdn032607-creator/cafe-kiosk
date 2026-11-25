package cafe.domain.member;

/** 기본 등급: 할인 없음, 적립 1%. 누적 10만 원이면 Silver, 30만 원이면 바로 Gold. */
public class Bronze extends RateBasedGrade {

    public Bronze() {
        super("BRONZE", 0, 1);
    }

    @Override
    public MembershipGrade evaluate(long totalSpent) {
        if (totalSpent >= Gold.THRESHOLD) {
            return new Gold();
        }
        if (totalSpent >= Silver.THRESHOLD) {
            return new Silver();
        }
        return this;
    }
}
