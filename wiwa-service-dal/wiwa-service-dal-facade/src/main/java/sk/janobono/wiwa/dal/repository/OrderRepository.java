package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.model.OrderDeliveryDo;
import sk.janobono.wiwa.dal.model.OrderTotalDo;

import java.util.Optional;

public interface OrderRepository {

    void deleteById(long id);

    Optional<Long> getOrderUserId(long id);

    Optional<OrderDo> findById(long id);

    OrderDo insert(OrderDo orderDo);

    void setDelivery(long id, OrderDeliveryDo orderDelivery);

    void setOrderTotal(long id, OrderTotalDo orderTotal);

    void setSummary(long id, String summary);
}
