package cafe.domain.discount;

import cafe.domain.member.MembershipGrade;

/** 회원 등급에 따른 할인. 계산은 등급 객체에게 맡긴다. */
public class GradeDiscount implements DiscountPolicy {

    private final MembershipGrade grade;

    public GradeDiscount(MembershipGrade grade) {
        this.grade = grade;
    }

    @Override
    public String name() {
        return grade.name() + " 등급 할인";
    }

    @Override
    public int discount(int amount) {
        return grade.discount(amount);
    }
}
