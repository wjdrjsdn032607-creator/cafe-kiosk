package cafe.domain.member;

/** 비회원 등급: 할인·적립이 없다. */
public class GuestGrade extends RateBasedGrade {

    public GuestGrade() {
        super("GUEST", 0, 0);
    }

    @Override
    public MembershipGrade evaluate(long totalSpent) {
        return this;
    }
}
