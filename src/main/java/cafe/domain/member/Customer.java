package cafe.domain.member;

/**
 * 주문하는 사람의 규약. 회원(Member)과 비회원(GuestMember)이 구현한다.
 * 비회원을 null로 두지 않고 객체(Null Object)로 만들어서 if (member == null) 분기를 없앤다.
 */
public interface Customer {

    String getId();

    String getName();

    MembershipGrade getGrade();

    int getPoints();

    long getTotalSpent();

    /** 결제 금액을 반영해 포인트를 적립하고 적립한 포인트를 돌려준다. */
    int earn(int paidAmount);

    void usePoints(int points);

    void restorePoints(int points);

    /** 주문 취소 시 적립했던 포인트와 누적 금액을 되돌린다. */
    void reverseEarn(int paidAmount, int earnedPoints);
}
