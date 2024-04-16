package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderItemAttributeDo;

import java.util.List;

public interface OrderItemAttributeRepository {

    List<OrderItemAttributeDo> findAllByOrderItemId(final Long orderItemId);

    OrderItemAttributeDo save(final OrderItemAttributeDo orderItemAttributeDo);
}
