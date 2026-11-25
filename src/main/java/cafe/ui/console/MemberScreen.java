package cafe.ui.console;

import cafe.common.CafeException;
import cafe.domain.member.Member;
import cafe.service.MemberService;

import java.io.PrintStream;
import java.util.Locale;

/** 회원 가입·조회 화면. */
public class MemberScreen {

    private final ConsoleInput in;
    private final PrintStream out;
    private final MemberService memberService;

    public MemberScreen(ConsoleInput in, PrintStream out, MemberService memberService) {
        this.in = in;
        this.out = out;
        this.memberService = memberService;
    }

    public void start() {
        out.println("1. 회원 가입   2. 회원 조회   0. 뒤로");
        int choice = in.readInt("> ", 0, 2);
        try {
            switch (choice) {
                case 1 -> register();
                case 2 -> lookup();
                default -> {
                }
            }
        } catch (CafeException | IllegalArgumentException e) {
            out.println("실패: " + e.getMessage());
        }
    }

    private void register() {
        String name = in.readLine("이름: ");
        Member member = memberService.register(name);
        out.println("가입했어요. 회원 번호: " + member.getId());
    }

    private void lookup() {
        String id = in.readLine("회원 번호: ");
        Member member = memberService.find(id);
        out.println(String.format(Locale.US, "%s (%s) / 등급 %s / 포인트 %,d / 누적 결제 %,d원",
                member.getName(), member.getId(), member.getGrade().name(),
                member.getPoints(), member.getTotalSpent()));
    }
}
