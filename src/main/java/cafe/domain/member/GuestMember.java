package cafe.domain.member;

import cafe.common.CafeException;

/** 비회원(Null Object). 적립·포인트 사용이 없어서 Order 쪽에 비회원 분기가 필요 없다. */
public class GuestMember implements Customer {

    private final MembershipGrade grade = new GuestGrade();

    @Override
    public String getId() {
        return "GUEST";
    }

    @Override
    public String getName() {
        return "비회원";
    }

    @Override
    public MembershipGrade getGrade() {
        return grade;
    }

    @Override
    public int getPoints() {
        return 0;
    }

    @Override
    public long getTotalSpent() {
        return 0;
    }

    @Override
    public int earn(int paidAmount) {
        return 0;
    }

    @Override
    public void usePoints(int points) {
        throw new CafeException("비회원은 포인트를 사용할 수 없어요.");
    }

    @Override
    public void restorePoints(int points) {
        // 비회원은 포인트가 없으므로 할 일이 없다.
    }

    @Override
    public void reverseEarn(int paidAmount, int earnedPoints) {
        // 비회원은 적립이 없으므로 할 일이 없다.
    }
}
