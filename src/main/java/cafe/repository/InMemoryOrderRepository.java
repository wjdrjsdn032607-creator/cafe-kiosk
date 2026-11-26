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

    /** 결제가 끝난 주문이 저장될 때만 번호를 하나 올린다. (실패한 주문은 번호를 쓰지 않는다) */
    @Override
    public void save(Order order) {
        if (!orders.containsKey(order.getId())) {
            sequence++;
        }
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
        // 번호를 미리 써버리지 않고 '다음에 쓸 번호'만 알려준다. 실제로 올리는 건 save()에서.
        return String.format("O-%04d", sequence + 1);
    }
}
