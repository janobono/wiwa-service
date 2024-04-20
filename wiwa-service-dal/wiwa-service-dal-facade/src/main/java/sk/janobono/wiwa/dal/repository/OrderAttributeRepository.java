package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderAttributeDo;
import sk.janobono.wiwa.model.OrderAttributeKey;

import java.util.List;
import java.util.Optional;

public interface OrderAttributeRepository {

    List<OrderAttributeDo> findAllByOrderId(Long orderId);

    Optional<OrderAttributeDo> findByOrderIdAndAttributeKey(Long orderId, OrderAttributeKey orderAttributeKey);

    OrderAttributeDo save(OrderAttributeDo orderAttributeDo);
}
