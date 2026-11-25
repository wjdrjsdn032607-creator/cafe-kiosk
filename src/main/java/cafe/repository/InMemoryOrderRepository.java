package cafe.repository;

import cafe.domain.order.Order;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Order> orders = new LinkedHashMap<>();
    private int sequence = 0;

    @Override
    public void save(Order order) {
        orders.put(order.getId(), order);
    }

    @Override
    public Optional<Order> findById(String id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        return orders.values().stream()
                .filter(order -> order.getCustomer().getId().equals(customerId))
                .toList();
    }

    @Override
    public String nextId() {
        sequence++;
        return String.format("O-%04d", sequence);
    }
}
