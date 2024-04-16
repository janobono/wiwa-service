package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderItemDo;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository {

    void deleteById(final Long id);

    boolean existsById(final Long id);

    List<OrderItemDo> findAllByOrderId(final Long orderId);

    Optional<OrderItemDo> findById(final Long id);

    OrderItemDo save(final OrderItemDo orderItemDo);
}
