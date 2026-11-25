package cafe.repository;

import cafe.domain.menu.MenuItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** 메모리(Map)에 저장하는 구현. DB 없이도 동작하고, 나중에 DB 구현으로 바꿔 끼울 수 있다. */
public class InMemoryMenuRepository implements MenuRepository {

    private final Map<String, MenuItem> items = new LinkedHashMap<>();

    @Override
    public void save(MenuItem item) {
        items.put(item.getId(), item);
    }

    @Override
    public List<MenuItem> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Optional<MenuItem> findById(String id) {
        return Optional.ofNullable(items.get(id));
    }
}
