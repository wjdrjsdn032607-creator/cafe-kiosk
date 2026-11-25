package cafe.ui.console;

import cafe.domain.menu.MenuItem;
import cafe.service.MenuService;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

/** 메뉴 목록 화면. 입력과 출력만 하고 계산은 하지 않는다. */
public class MenuScreen {

    private final MenuService menuService;
    private final PrintStream out;

    public MenuScreen(MenuService menuService, PrintStream out) {
        this.menuService = menuService;
        this.out = out;
    }

    public void show() {
        List<MenuItem> menu = menuService.findAll();
        out.println();
        out.println("[메뉴]");
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.get(i);
            String note = item.supportsSize() ? "  (S 사이즈 기준)" : "";
            out.println(String.format(Locale.US, "%2d. %s  %,d원%s",
                    i + 1, item.getName(), item.getBasePrice(), note));
        }
    }
}
