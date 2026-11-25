package cafe.ui.console;

import cafe.common.CafeException;
import cafe.domain.menu.MenuItem;
import cafe.domain.menu.Option;
import cafe.domain.menu.Size;
import cafe.domain.menu.Temperature;
import cafe.domain.order.Order;
import cafe.domain.order.Receipt;
import cafe.domain.payment.PaymentType;
import cafe.service.MenuService;
import cafe.service.OrderItemRequest;
import cafe.service.OrderRequest;
import cafe.service.OrderService;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** 주문·취소 화면. 입력을 받아 OrderService에 넘기고, 결과(영수증 문자열)를 출력한다. */
public class OrderScreen {

    private final ConsoleInput in;
    private final PrintStream out;
    private final MenuService menuService;
    private final MenuScreen menuScreen;
    private final OrderService orderService;

    public OrderScreen(ConsoleInput in, PrintStream out, MenuService menuService,
                       MenuScreen menuScreen, OrderService orderService) {
        this.in = in;
        this.out = out;
        this.menuService = menuService;
        this.menuScreen = menuScreen;
        this.orderService = orderService;
    }

    public void start() {
        List<MenuItem> menu = menuService.findAll();
        List<OrderItemRequest> items = new ArrayList<>();
        try {
            while (true) {
                menuScreen.show();
                int number = in.readInt("메뉴 번호 (0: 주문 확정) > ", 0, menu.size());
                if (number == 0) {
                    break;
                }
                items.add(readItem(menu.get(number - 1)));
                out.println("  담았어요. (현재 " + items.size() + "종류)");
            }
            if (items.isEmpty()) {
                out.println("담은 메뉴가 없어서 주문을 취소했어요.");
                return;
            }
            checkoutWithRetry(items);
        } catch (CafeException | IllegalArgumentException e) {
            out.println("주문 실패: " + e.getMessage());
        }
    }

    /**
     * 회원 번호·쿠폰은 입력하는 즉시 확인해서, 틀리면 그 칸만 다시 물어본다.
     * 결제(예: 포인트 부족)에서 실패하면 담아둔 메뉴는 그대로 두고 결제 수단만 다시 고르게 한다.
     */
    private void checkoutWithRetry(List<OrderItemRequest> items) {
        String memberId = readMemberId();
        String couponCode = readCouponCode();
        while (true) {
            PaymentType paymentType = readPaymentType();
            try {
                Receipt receipt = orderService.placeOrder(
                        new OrderRequest(memberId, items, couponCode, paymentType));
                out.println();
                out.println(receipt.format());
                return;
            } catch (CafeException e) {
                out.println("결제 실패: " + e.getMessage());
                out.println("1. 결제 수단 다시 고르기   0. 주문 취소");
                if (in.readInt("> ", 0, 1) == 0) {
                    out.println("주문을 취소했어요.");
                    return;
                }
            }
        }
    }

    /** 있는 회원 번호(또는 비회원 엔터)가 들어올 때까지 다시 묻는다. */
    private String readMemberId() {
        while (true) {
            String memberId = in.readLine("회원 번호 (비회원은 엔터): ");
            try {
                orderService.checkMember(memberId);
                return memberId;
            } catch (CafeException e) {
                out.println("  " + e.getMessage() + " 다시 입력해 주세요.");
            }
        }
    }

    /** 쓸 수 있는 쿠폰(또는 없으면 엔터)이 들어올 때까지 다시 묻는다. */
    private String readCouponCode() {
        while (true) {
            String couponCode = in.readLine("쿠폰 코드 (없으면 엔터): ");
            try {
                orderService.checkCoupon(couponCode);
                return couponCode;
            } catch (CafeException e) {
                out.println("  " + e.getMessage() + " 다시 입력해 주세요.");
            }
        }
    }

    public void history() {
        String memberId = in.readLine("회원 번호 (전체 조회는 엔터): ");
        List<Order> orders = orderService.findOrders(memberId);
        if (orders.isEmpty()) {
            out.println("주문 내역이 없어요.");
            return;
        }
        for (Order order : orders) {
            String names = order.getLines().stream()
                    .map(line -> line.item().description()
                            + (line.quantity() > 1 ? " x" + line.quantity() : ""))
                    .collect(Collectors.joining(", "));
            out.println(String.format(Locale.US, "%s [%s] %s / %s / %,d원",
                    order.getId(), order.getStatus().getLabel(),
                    order.getCustomer().getName(), names, order.getPaidAmount()));
        }
        String orderId = in.readLine("취소할 주문번호 (건너뛰기: 엔터): ");
        if (orderId.isEmpty()) {
            return;
        }
        try {
            Receipt receipt = orderService.cancelOrder(orderId);
            out.println("주문을 취소했어요.");
            out.println(receipt.format());
        } catch (CafeException | IllegalArgumentException e) {
            out.println("취소 실패: " + e.getMessage());
        }
    }

    private OrderItemRequest readItem(MenuItem item) {
        Size size = null;
        Temperature temperature = null;
        List<Option> options = List.of();

        if (item.supportsSize()) {
            Size[] sizes = Size.values();
            StringBuilder prompt = new StringBuilder("사이즈: ");
            for (int i = 0; i < sizes.length; i++) {
                prompt.append(i + 1).append('.').append(sizes[i].getLabel())
                        .append("(+").append(sizes[i].getSurcharge()).append(")  ");
            }
            out.println(prompt);
            size = sizes[in.readInt("사이즈 번호 > ", 1, sizes.length) - 1];
        }

        List<Temperature> temperatures = new ArrayList<>(item.allowedTemperatures());
        if (temperatures.size() == 1) {
            temperature = temperatures.get(0);
        } else if (temperatures.size() > 1) {
            StringBuilder prompt = new StringBuilder("온도: ");
            for (int i = 0; i < temperatures.size(); i++) {
                prompt.append(i + 1).append('.').append(temperatures.get(i).getLabel()).append("  ");
            }
            out.println(prompt);
            temperature = temperatures.get(in.readInt("온도 번호 > ", 1, temperatures.size()) - 1);
        }

        if (item.supportsOptions()) {
            options = readOptions();
        }
        int quantity = in.readInt("수량 > ", 1, 20);
        return new OrderItemRequest(item.getId(), size, temperature, options, quantity);
    }

    private List<Option> readOptions() {
        while (true) {
            String line = in.readLine("옵션 (샷추가, 시럽, 휘핑 / 없으면 엔터): ");
            try {
                List<Option> options = new ArrayList<>();
                for (String token : line.split(",")) {
                    if (!token.isBlank()) {
                        options.add(Option.fromLabel(token));
                    }
                }
                return options;
            } catch (IllegalArgumentException e) {
                out.println(e.getMessage());
            }
        }
    }

    private PaymentType readPaymentType() {
        PaymentType[] types = PaymentType.values();
        StringBuilder prompt = new StringBuilder("결제 수단: ");
        for (int i = 0; i < types.length; i++) {
            prompt.append(i + 1).append('.').append(types[i].getLabel()).append("  ");
        }
        out.println(prompt);
        return types[in.readInt("결제 수단 번호 > ", 1, types.length) - 1];
    }
}
