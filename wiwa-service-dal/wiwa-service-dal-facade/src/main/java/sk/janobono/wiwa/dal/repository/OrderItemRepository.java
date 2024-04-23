package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderItemDo;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository {

    void deleteById(long id);

    Optional<OrderItemDo> findById(long id);

    List<OrderItemDo> findAllByOrderId(long orderId);

    OrderItemDo save(OrderItemDo orderItemDo);

    void saveAll(List<OrderItemDo> batch);
}
