package cafe.domain.member;

import cafe.common.CafeException;

/**
 * 회원. 포인트·누적 금액·등급은 private이고, earn/usePoints 같은 메소드로만 바뀐다.
 * 승급은 earn() 안에서 자동으로 일어난다.
 */
public class Member implements Customer {

    private final String id;
    private final String name;
    private int points;
    private long totalSpent;
    private MembershipGrade grade;

    public Member(String id, String name) {
        this(id, name, 0, 0L);
    }

    public Member(String id, String name, int points, long totalSpent) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("회원 ID가 비어 있어요.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("회원 이름이 비어 있어요.");
        }
        if (points < 0 || totalSpent < 0) {
            throw new IllegalArgumentException("포인트와 누적 금액은 0 이상이어야 해요.");
        }
        this.id = id;
        this.name = name;
        this.points = points;
        this.totalSpent = totalSpent;
        this.grade = MembershipGrade.gradeFor(totalSpent);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MembershipGrade getGrade() {
        return grade;
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public long getTotalSpent() {
        return totalSpent;
    }

    @Override
    public int earn(int paidAmount) {
        if (paidAmount < 0) {
            throw new IllegalArgumentException("결제 금액은 0원 이상이어야 해요.");
        }
        int earned = grade.earnPoints(paidAmount);
        points += earned;
        totalSpent += paidAmount;
        grade = grade.evaluate(totalSpent);
        return earned;
    }

    @Override
    public void usePoints(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("사용 포인트는 0 이상이어야 해요.");
        }
        if (amount > points) {
            throw new CafeException("포인트가 부족해요. (보유 " + points + ", 필요 " + amount + ")");
        }
        points -= amount;
    }

    @Override
    public void restorePoints(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("환원 포인트는 0 이상이어야 해요.");
        }
        points += amount;
    }

    @Override
    public void reverseEarn(int paidAmount, int earnedPoints) {
        points = Math.max(0, points - earnedPoints);
        totalSpent = Math.max(0, totalSpent - paidAmount);
        // 누적 금액이 줄었으니 등급도 다시 계산한다. (기준 아래로 내려가면 강등)
        grade = MembershipGrade.gradeFor(totalSpent);
    }
}
