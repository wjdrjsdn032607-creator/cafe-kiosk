package cafe.ui.console;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Scanner를 감싸서 입력 형식(숫자인지, 범위 안인지)만 확인한다.
 * 업무 규칙(포인트 부족, 없는 회원 등)은 여기서 검사하지 않고 도메인이 예외로 알려준다.
 * 입력이 끝나면(EOF) NoSuchElementException이 그대로 올라가고, ConsoleApp이 받아서 종료한다.
 */
public class ConsoleInput {

    private final Scanner scanner;
    private final PrintStream out;

    public ConsoleInput(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in, StandardCharsets.UTF_8);
        this.out = out;
    }

    public String readLine(String prompt) {
        out.print(prompt);
        out.flush();
        return scanner.nextLine().trim();
    }

    /** min~max 사이의 정수가 입력될 때까지 다시 묻는다. */
    public int readInt(String prompt, int min, int max) {
        while (true) {
            String line = readLine(prompt);
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                out.println(min + " ~ " + max + " 사이의 숫자를 입력해 주세요.");
            } catch (NumberFormatException e) {
                out.println("숫자를 입력해 주세요.");
            }
        }
    }
}
