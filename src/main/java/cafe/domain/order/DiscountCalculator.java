package cafe.domain.order;

import cafe.domain.discount.DiscountPolicy;
import cafe.domain.discount.GradeDiscount;
import cafe.domain.member.Customer;

import java.util.ArrayList;
import java.util.List;

/**
 * 회원 등급과 할인 정책에 따른 할인 금액 계산을 담당한다.
 */
public final class DiscountCalculator {

    public static Result calculate(
            int subtotal,
            Customer customer,
            List<DiscountPolicy> extraPolicies) {

        List<DiscountPolicy> policies = new ArrayList<>();
        policies.add(new GradeDiscount(customer.getGrade()));
        policies.addAll(extraPolicies);

        List<DiscountLine> lines = new ArrayList<>();
        int running = subtotal;

        for (DiscountPolicy policy : policies) {
            int discount = Math.min(
                    policy.discount(running), running);

            if (discount > 0) {
                lines.add(new DiscountLine(
                        policy.name(), discount));
                running -= discount;
            }
        }

        return new Result(
                running,
                List.copyOf(lines),
                List.copyOf(policies));
    }

    public record Result(
            int finalAmount,
            List<DiscountLine> lines,
            List<DiscountPolicy> policies) {
    }
}
