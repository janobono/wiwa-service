package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderAttributeDo;

import java.util.List;

public interface OrderAttributeRepository {

    List<OrderAttributeDo> findAllByOrderId(Long orderId);

    OrderAttributeDo save(OrderAttributeDo orderAttributeDo);
}
