package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderAttributeDo;

import java.util.List;

public interface OrderAttributeRepository {

    List<OrderAttributeDo> findAllByOrderId(final Long orderId);

    OrderAttributeDo save(final OrderAttributeDo orderAttributeDo);
}
