package cafe;

import cafe.domain.discount.DiscountPolicy;
import cafe.domain.discount.HappyHourDiscount;
import cafe.repository.InMemoryCouponRepository;
import cafe.repository.InMemoryMemberRepository;
import cafe.repository.InMemoryMenuRepository;
import cafe.repository.InMemoryOrderRepository;
import cafe.service.MemberService;
import cafe.service.MenuService;
import cafe.service.OrderService;
import cafe.ui.console.ConsoleApp;
import cafe.ui.console.ConsoleInput;
import cafe.ui.console.MemberScreen;
import cafe.ui.console.MenuScreen;
import cafe.ui.console.OrderScreen;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalTime;
import java.util.List;

/** 객체를 조립(의존성 주입)하고 콘솔 앱을 시작한다. 나중에 웹을 얹을 때도 이 조립 코드를 재사용한다. */
public class Main {

    public static void main(String[] args) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        Clock clock = Clock.systemDefaultZone();

        InMemoryMenuRepository menuRepository = new InMemoryMenuRepository();
        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
        InMemoryCouponRepository couponRepository = new InMemoryCouponRepository();
        SampleData.load(menuRepository, memberRepository, couponRepository, clock);

        List<DiscountPolicy> automaticPolicies = List.of(
                new HappyHourDiscount(LocalTime.of(14, 0), LocalTime.of(16, 0), 10, clock));

        MenuService menuService = new MenuService(menuRepository);
        MemberService memberService = new MemberService(memberRepository);
        OrderService orderService = new OrderService(menuRepository, memberRepository,
                orderRepository, couponRepository, automaticPolicies, clock);

        ConsoleInput in = new ConsoleInput(System.in, out);
        MenuScreen menuScreen = new MenuScreen(menuService, out);
        OrderScreen orderScreen = new OrderScreen(in, out, menuService, menuScreen, orderService);
        MemberScreen memberScreen = new MemberScreen(in, out, memberService);

        new ConsoleApp(in, out, menuScreen, orderScreen, memberScreen).run();
    }
}
