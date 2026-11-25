package cafe.domain.order;

import cafe.domain.menu.MenuItem;
import cafe.domain.menu.PriceLine;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 영수증. 출력은 하지 않고 문자열(format())만 만들어 준다.
 * 콘솔이든 웹이든 출력은 ui 계층이 담당한다.
 */
public class Receipt {

    private static final int WIDTH = 38;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Order order;

    Receipt(Order order) {
        this.order = order;
    }

    public String getOrderId() {
        return order.getId();
    }

    public int getPaidAmount() {
        return order.getPaidAmount();
    }

    public int getEarnedPoints() {
        return order.getEarnedPoints();
    }

    public String format() {
        StringBuilder sb = new StringBuilder();
        String bar = "=".repeat(WIDTH);
        String line = "-".repeat(WIDTH);

        sb.append(bar).append('\n');
        sb.append("주문번호: ").append(order.getId())
                .append("   ").append(order.getCreatedAt().format(TIME_FORMAT)).append('\n');
        sb.append("고객: ").append(order.getCustomer().getName())
                .append(" (").append(order.getCustomer().getGrade().name()).append(")\n");
        sb.append(line).append('\n');

        for (MenuItem item : order.getItems()) {
            for (PriceLine priceLine : item.priceLines()) {
                sb.append(row(priceLine.label(), money(priceLine.amount()))).append('\n');
            }
        }
        sb.append(line).append('\n');
        sb.append(row("소계", money(order.subtotal()))).append('\n');
        for (DiscountLine discount : order.getDiscountLines()) {
            sb.append(row(discount.name(), "-" + money(discount.amount()))).append('\n');
        }
        sb.append(line).append('\n');
        sb.append(row("결제 금액 (" + order.getPaymentName() + ")", money(order.getPaidAmount()))).append('\n');
        sb.append(row("적립 포인트", money(order.getEarnedPoints()))).append('\n');
        if (order.getStatus() == OrderStatus.CANCELLED) {
            sb.append("※ 취소된 주문이에요.\n");
        }
        sb.append(bar);
        return sb.toString();
    }

    private static String money(int amount) {
        return String.format(Locale.US, "%,d", amount);
    }

    private static String row(String label, String amount) {
        int pad = Math.max(1, WIDTH - displayWidth(label) - amount.length());
        return label + " ".repeat(pad) + amount;
    }

    /** 한글처럼 폭이 넓은 문자는 2칸으로 계산해서 줄을 맞춘다. */
    private static int displayWidth(String text) {
        int width = 0;
        for (char c : text.toCharArray()) {
            width += c >= 0x1100 ? 2 : 1;
        }
        return width;
    }
}
