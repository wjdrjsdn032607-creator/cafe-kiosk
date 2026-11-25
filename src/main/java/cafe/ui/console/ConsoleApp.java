package cafe.ui.console;

import java.io.PrintStream;
import java.util.NoSuchElementException;

/** 메인 메뉴를 반복하고 번호에 따라 화면 클래스로 연결한다. */
public class ConsoleApp {

    private final ConsoleInput in;
    private final PrintStream out;
    private final MenuScreen menuScreen;
    private final OrderScreen orderScreen;
    private final MemberScreen memberScreen;

    public ConsoleApp(ConsoleInput in, PrintStream out, MenuScreen menuScreen,
                      OrderScreen orderScreen, MemberScreen memberScreen) {
        this.in = in;
        this.out = out;
        this.menuScreen = menuScreen;
        this.orderScreen = orderScreen;
        this.memberScreen = memberScreen;
    }

    public void run() {
        try {
            while (true) {
                printMainMenu();
                int choice = in.readInt("> ", 0, 4);
                switch (choice) {
                    case 1 -> menuScreen.show();
                    case 2 -> orderScreen.start();
                    case 3 -> memberScreen.start();
                    case 4 -> orderScreen.history();
                    default -> {
                        out.println("이용해 주셔서 감사합니다!");
                        return;
                    }
                }
            }
        } catch (NoSuchElementException e) {
            out.println();
            out.println("입력이 끝나서 프로그램을 종료해요.");
        }
    }

    private void printMainMenu() {
        out.println();
        out.println("===== 카페 키오스크 =====");
        out.println("1. 메뉴 보기");
        out.println("2. 주문하기");
        out.println("3. 회원 가입 / 조회");
        out.println("4. 주문 내역 / 취소");
        out.println("0. 종료");
    }
}
