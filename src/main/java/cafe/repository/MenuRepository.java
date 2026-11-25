package cafe.repository;

import cafe.domain.menu.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuRepository {

    void save(MenuItem item);

    List<MenuItem> findAll();

    Optional<MenuItem> findById(String id);
}
