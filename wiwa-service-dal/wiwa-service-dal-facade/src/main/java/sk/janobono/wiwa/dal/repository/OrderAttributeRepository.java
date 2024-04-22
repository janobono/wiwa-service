package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderAttributeDo;
import sk.janobono.wiwa.model.OrderAttributeKey;

import java.util.Optional;

public interface OrderAttributeRepository {

    Optional<OrderAttributeDo> findByOrderIdAndAttributeKey(long orderId, OrderAttributeKey orderAttributeKey);

    OrderAttributeDo save(OrderAttributeDo orderAttributeDo);
}
