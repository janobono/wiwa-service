package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderDo;

import java.util.Optional;

public interface OrderRepository {

    void deleteById(long id);

    Optional<Long> getOrderUserId(long id);

    Optional<OrderDo> findById(long id);

    OrderDo save(OrderDo orderDo);
}
