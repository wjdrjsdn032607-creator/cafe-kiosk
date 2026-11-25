package cafe.service;

import cafe.domain.menu.MenuItem;
import cafe.repository.MenuRepository;

import java.util.List;

public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<MenuItem> findAll() {
        return menuRepository.findAll();
    }
}
