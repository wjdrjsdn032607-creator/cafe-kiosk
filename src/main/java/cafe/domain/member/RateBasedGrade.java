package cafe.domain.member;

/**
 * 할인율·적립률(%)로 계산하는 등급의 공통 부모. 공통 계산은 여기서 재사용하고,
 * 등급마다 다른 규칙(승급 조건, 보너스)은 자식이 오버라이딩한다.
 */
public abstract class RateBasedGrade implements MembershipGrade {

    private final String name;
    private final int discountPercent;
    private final int pointPercent;

    protected RateBasedGrade(String name, int discountPercent, int pointPercent) {
        if (discountPercent < 0 || discountPercent > 100 || pointPercent < 0 || pointPercent > 100) {
            throw new IllegalArgumentException("비율은 0~100 사이여야 해요.");
        }
        this.name = name;
        this.discountPercent = discountPercent;
        this.pointPercent = pointPercent;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int discount(int amount) {
        return amount * discountPercent / 100;
    }

    @Override
    public int earnPoints(int paidAmount) {
        return paidAmount * pointPercent / 100;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public int getPointPercent() {
        return pointPercent;
    }

    @Override
    public String toString() {
        return name;
    }
}
