package cafe.domain.member;

/**
 * 회원 등급 규약(인터페이스).
 * 할인, 적립, 승급 규칙을 등급 객체가 스스로 계산한다. Order와 Member는 구체 등급(Gold 등)을 모른다.
 */
public interface MembershipGrade {

    String name();

    /** 결제 전 금액에 대한 등급 할인 금액. */
    int discount(int amount);

    /** 결제 금액에 대한 포인트 적립량. */
    int earnPoints(int paidAmount);

    /** 누적 결제 금액으로 승급을 판단해서, 승급했다면 새 등급을, 아니면 자기 자신을 돌려준다. */
    MembershipGrade evaluate(long totalSpent);

    /** 누적 결제 금액에 맞는 등급을 찾는다. */
    static MembershipGrade gradeFor(long totalSpent) {
        MembershipGrade grade = new Bronze();
        MembershipGrade next = grade.evaluate(totalSpent);
        while (next != grade) {
            grade = next;
            next = grade.evaluate(totalSpent);
        }
        return grade;
    }
}
