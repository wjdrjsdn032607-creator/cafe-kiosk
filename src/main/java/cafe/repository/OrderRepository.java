package cafe.repository;

import cafe.domain.order.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    void save(Order order);

    Optional<Order> findById(String id);

    List<Order> findAll();

    List<Order> findByCustomerId(String customerId);

    /** 새 주문번호를 만든다 (O-0001, O-0002 ...). */
    String nextId();
}
