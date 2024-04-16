package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderContactDo;

import java.util.Optional;

public interface OrderContactRepository {

    Optional<OrderContactDo> findByOrderId(final Long orderId);

    OrderContactDo save(final OrderContactDo orderContactDo);
}
