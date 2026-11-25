package cafe.repository;

import cafe.domain.order.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    void save(Order order);

    Optional<Order> findById(String id);

    List<Order> findAll();

    List<Order> findByCustomerId(String customerId);

    /** 다음 주문번호를 알려준다 (O-0001, O-0002 ...). 번호는 save()로 주문이 저장될 때 확정된다. */
    String nextId();
}
